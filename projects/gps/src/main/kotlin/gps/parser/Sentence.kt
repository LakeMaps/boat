package gps.parser

import java.util.Locale

/**
 * A NMEA 0183 sentence.
 *
 * This representation does not support messages that have special encapsulation as it assumes
 * that the prefix of the message is '$' and that there is a set of fields.
 *
 * @param talker the identifier of the "talker"
 * @param type the message type
 * @param fields an array of fields in the message
 * @property checksum the checksum of this sentence
 * @constructor Constructs a sentence with the given talker, type, and fields
 */
class Sentence(val talker: CharArray, val type: CharArray, val fields: Array<CharArray>) {
    companion object {
        const val MESSAGE_START = 0x24.toChar()
        const val MESSAGE_END1 = 0x0D.toChar()
        const val MESSAGE_END2 = 0x0A.toChar()
        const val FIELD_DELIMITER = 0x2C.toChar()
        const val CHECKSUM_DELIMITER = 0x2A.toChar()
    }

    constructor(talker: String, type: String, fields: Array<String>)
        : this(talker.toCharArray(), type.toCharArray(), fields.map(String::toCharArray).toTypedArray())

    private val data: CharArray by lazy {
        fields.fold(charArrayOf(), { acc, chars -> acc + FIELD_DELIMITER + chars })
    }

    val checksum: Int by lazy {
        checksum(talker + type + data)
    }

    private fun checksum(chars: CharArray): Int {
        return chars.fold(0, { acc, c -> acc.xor(c.toInt()) })
    }

    override fun toString(): String {
        val cs = String.format(Locale.ENGLISH, "%s%02X", CHECKSUM_DELIMITER, checksum)
        return (talker + type + data).joinToString(prefix = MESSAGE_START.toString(), separator = "", postfix = cs)
    }
}
