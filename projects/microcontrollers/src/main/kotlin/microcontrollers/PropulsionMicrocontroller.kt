package microcontrollers

import java.nio.ByteBuffer
import java.util.concurrent.locks.Lock

class PropulsionMicrocontroller(override val lock: Lock, override val recv: () -> Byte, override val send: (ByteArray) -> Unit) : Microcontroller {
    override val payloadSizes: Map<Byte, Int> = mapOf(
        0x10.toByte() to 1,
        0x13.toByte() to 4,
        0x1F.toByte() to 1
    )

    fun reset(): Message {
        return writeMessage(Message(0x10, byteArrayOf(0x00)))
    }

    fun setSpeed(m0: Short, m1: Short): Message {
        val buffer = ByteBuffer.allocate(4).putShort(m0).putShort(m1).array()
        return writeMessage(Message(0x13, buffer))
    }
}
