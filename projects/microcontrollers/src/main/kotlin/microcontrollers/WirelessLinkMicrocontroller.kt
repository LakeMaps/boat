package microcontrollers

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
        return WirelessLinkReceiveMessage(writeMessage(Message(0x03, byteArrayOf(0x00))))
    }

    fun send(bytes: ByteArray): Message {
        if (bytes.size > 61) {
            throw IllegalArgumentException("Message payload must be !> 61 bytes, ${bytes.size} given")
        }
        return writeMessage(Message(0x04, bytes))
    }
}
