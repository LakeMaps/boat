package microcontrollers

import log.Log

import java.util.concurrent.locks.Lock

class WirelessLinkMicrocontroller(override val lock: Lock, override val recv: () -> Byte, override val send: (ByteArray) -> Unit) : Microcontroller {
    override val payloadSizes: Map<Byte, Int> = mapOf(
        0x00.toByte() to 1,
        0x03.toByte() to 64,
        0x04.toByte() to 1,
        0x0F.toByte() to 1
    )

    fun reset(): Message {
        return writeMessage(Message(0x00, byteArrayOf(0x00)))
    }

    fun receive(): WirelessLinkReceiveMessage {
        val req = Message(0x03, byteArrayOf(0x00))
        val res = writeMessage(req)
        return when (res.command.toInt()) {
            0x03 -> WirelessLinkReceiveMessage(res)
            else -> {
                Log.w { "Incorrect response for $req, received $res" }
                WirelessLinkReceiveMessage(Message(0x03, ByteArray(payloadSizes[0x03]!!, { 0 })))
            }
        }
    }

    fun send(bytes: ByteArray): Message {
        if (bytes.size > 61) {
            throw IllegalArgumentException("Message payload must be !> 61 bytes, ${bytes.size} given")
        }

        val req = Message(0x04, bytes)
        val res = writeMessage(req)
        return when (res.command.toInt()) {
            0x04 -> res
            else -> {
                Log.w { "Incorrect response for $req, received $res" }
                Message(0x04, byteArrayOf(0x00))
            }
        }
    }
}
