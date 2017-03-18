package microcontrollers

class Message(val command: Byte, val payload: ByteArray) {
    companion object Constants {
        const val MESSAGE_START = 0xAA.toByte()
    }

    val bytes: ByteArray by lazy {
        val body = arrayOf(MESSAGE_START, command).toByteArray() + payload
        body + crc16(body)
    }

    val checksum: ByteArray by lazy {
        bytes.sliceArray((bytes.lastIndex - 1)..bytes.lastIndex)
    }

    override fun toString() = "Message(command=0x${command.hex()}, payload=0x${payload.hex()}, checksum=0x${checksum.hex()})"

    private fun crc16(bytes: ByteArray): ByteArray {
        var result: Int = 0
        for (b in bytes.map(Byte::toInt)) {
            for (i in 0..7) {
                val bit = (b shr (7 - i) and 1) == 1
                val c15 = (result shr 15 and 1) == 1
                result = result shl 1
                if (c15 xor bit) {
                    result = result xor 4129
                }
            }
        }

        result = result and 0xFFFF
        return arrayOf((result shr 8).toByte(), result.toByte()).toByteArray()
    }
}
