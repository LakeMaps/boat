package gps.parser

internal sealed class SentenceState {
    companion object {
        const val MAXIMUM_SENTENCE_LENGTH = 82
    }

    internal class Prefix : SentenceState() {
        fun next(recv: () -> Char): SentenceState {
            val char = recv()
            if (char == Sentence.MESSAGE_START) {
                return SentenceState.Talker()
            } else {
                return SentenceState.Prefix()
            }
        }
    }

    internal class Talker: SentenceState() {
        fun next(recv: () -> Char): SentenceState {
            val talker = charArrayOf(recv(), recv())
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
            // This next char should be field delimiter and we'll throw it away
            recv()
            return SentenceState.Fields(talker, type)
        }
    }

    internal class Fields(val talker: CharArray, val type: CharArray): SentenceState() {
        fun next(recv: () -> Char): SentenceState {
            val fields = mutableListOf<CharArray>()
            var field = mutableListOf<Char>()
            var count = 0
            while (true) {
                val char = recv()
                count++

                if (char == Sentence.FIELD_DELIMITER) {
                    fields.add(field.toCharArray())
                    field = mutableListOf<Char>()
                    continue
                }
                if (char == Sentence.CHECKSUM_DELIMITER) {
                    fields.add(field.toCharArray())
                    return SentenceState.Checksum(talker, type, fields.toTypedArray())
                }
                // We've seen 7 chars from previous states
                if ((count + 7) > MAXIMUM_SENTENCE_LENGTH) {
                    return SentenceState.Prefix()
                }

                field.add(char)
            }
        }
    }

    internal class Checksum(val talker: CharArray, val type: CharArray, val fields: Array<CharArray>): SentenceState() {
        fun next(recv: () -> Char): SentenceState {
            // TODO: validate the checksum
            @Suppress("UNUSED_VARIABLE")
            val checksum = charArrayOf(recv(), recv())
            return SentenceState.Complete(talker, type, fields)
        }
    }

    internal class Complete(val talker: CharArray, val type: CharArray, val fields: Array<CharArray>): SentenceState()
}
