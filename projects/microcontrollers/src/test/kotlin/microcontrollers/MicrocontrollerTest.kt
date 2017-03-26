@file:Suppress("DEPRECATION")

package microcontrollers

import kotlin.test.CollectionAssertionSession
import kotlin.test.shouldBe

import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

import org.junit.Test

private class SimpleMicrocontroller(
    override val lock: Lock,
    override val recv: () -> Byte,
    override val send: (ByteArray) -> Unit
) : Microcontroller
{
    override val payloadSizes: Map<Byte, Int>
        get() = mapOf(0x10.toByte() to 0x01)
}

class MicrocontrollerTest {
    @Test
    fun readMessageDoesSkipCorruptMessages() {
        val receiveBuffer = byteArrayOf(0xAA.toByte(), 0x10, 0x42, 0x79, 0x2E, 0xAA.toByte(), 0x10, 0x00, 0x79, 0x2E)
        val expectedMsg = byteArrayOf(0xAA.toByte(), 0x10, 0x00, 0x79, 0x2E)

        var recvCallCount = 0
        val recv = { receiveBuffer[recvCallCount++] }
        val sent = mutableListOf<Byte>()
        val microcontroller = SimpleMicrocontroller(
            ReentrantLock(), recv, { bytes -> bytes.forEach { sent.add(it) } })

        val message = microcontroller.writeMessage(Message(0x10, byteArrayOf(0x00)))
        CollectionAssertionSession(message.bytes.asIterable()).shouldBe(expectedMsg.asIterable())
    }
}
