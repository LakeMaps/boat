package gps.parser

import java.util.Arrays

internal sealed class SentenceState {
    companion object {
        const val MAXIMUM_SENTENCE_LENGTH = 82
    }

    internal class Prefix : SentenceState() {
        fun next(recv: () -> Char): SentenceState {
            val char = recv()
            return if (char == Sentence.MESSAGE_START) {
                SentenceState.Talker()
            } else {
                SentenceState.Prefix()
            }
        }
    }

    internal class Talker: SentenceState() {
        private val talkerIdPM = charArrayOf('P', 'M')

        fun next(recv: () -> Char): SentenceState {
            var talker = charArrayOf(recv(), recv())
            if (Arrays.equals(talker, talkerIdPM)) {
                // The PM Talker ID is four bytes
                talker += charArrayOf(recv(), recv())
            }
            return SentenceState.MessageType(talker)
        }
    }

    internal class MessageType(val talker: CharArray): SentenceState() {
        fun next(recv: () -> Char): SentenceState {
            // Instead of validating this message's type and trying to pair it with the
            // number of fields we should expect, we'll not do any of that and take as
            // many fields as we can in the next state and leave validation for a later time.
            // Essentially, we're operating under the assumption that most messages are
            // valid with the correct fields and whatnot. If that turns out to not be the
            // case, we might regret this decision.
            val type = charArrayOf(recv(), recv(), recv())
            return when (recv()) {
                Sentence.FIELD_DELIMITER -> SentenceState.Fields(talker, type)
                Sentence.CHECKSUM_DELIMITER -> SentenceState.Checksum(talker, type, arrayOf())
                // We seem to have recv'd something incorrect
                else -> SentenceState.Prefix()
            }
        }
    }

    internal class Fields(val talker: CharArray, val type: CharArray): SentenceState() {
        private val bytesReadInPreviousStates = 7

        fun next(recv: () -> Char): SentenceState {
            val fields = mutableListOf<CharArray>()
            var field = mutableListOf<Char>()
            var bytesRead = 0
            while (true) {
                val char = recv()
                bytesRead++

                if (char == Sentence.FIELD_DELIMITER) {
                    fields.add(field.toCharArray())
                    field = mutableListOf()
                    continue
                }
                if (char == Sentence.CHECKSUM_DELIMITER) {
                    fields.add(field.toCharArray())
                    return SentenceState.Checksum(talker, type, fields.toTypedArray())
                }
                if (char == Sentence.MESSAGE_END1 || char == Sentence.MESSAGE_END2) {
                    fields.add(field.toCharArray())
                    return SentenceState.Complete(Sentence(talker, type, fields.toTypedArray()))
                }
                if ((bytesRead + bytesReadInPreviousStates) > MAXIMUM_SENTENCE_LENGTH) {
                    return SentenceState.Prefix()
                }

                field.add(char)
            }
        }
    }

    internal class Checksum(val talker: CharArray, val type: CharArray, val fields: Array<CharArray>): SentenceState() {
        private val hexadecimalRadix = 16

        fun next(recv: () -> Char): SentenceState {
            val checksum = Integer.parseInt(String(charArrayOf(recv(), recv())), hexadecimalRadix)
            val sentence = Sentence(talker, type, fields)
            return when (checksum) {
                sentence.checksum -> SentenceState.Complete(sentence)
                else -> SentenceState.Prefix()
            }
        }
    }

    internal class Complete(val sentence: Sentence): SentenceState()
}
