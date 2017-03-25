package microcontrollers

import java.nio.ByteBuffer

class WirelessLinkReceiveMessage(private val message: Message) {
    val containsMessage = message.payload.first() == 1.toByte()

    val body = message.payload.sliceArray(1..61)

    val rssi = message.payload.sliceArray((message.payload.lastIndex - 1)..message.payload.lastIndex).toInt()

    internal val bytes = message.bytes

    override fun toString() = message.toString()

    private fun ByteArray.toInt(): Int {
        return ByteBuffer.wrap(this).short.toInt()
    }
}
