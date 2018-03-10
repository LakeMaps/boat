package keyv

object Bytes {
    fun from(number: Long): ByteArray {
        var value = number
        val result = ByteArray(8)
        for (i in 7 downTo 0) {
            result[i] = (value and 0xffL).toByte()
            value = value shr 8
        }
        return result
    }
}
