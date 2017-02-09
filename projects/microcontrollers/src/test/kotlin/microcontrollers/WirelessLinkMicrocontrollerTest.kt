@file:Suppress("DEPRECATION")

package microcontrollers

import org.junit.Test
import java.util.concurrent.locks.ReentrantLock
import kotlin.test.CollectionAssertionSession
import kotlin.test.shouldBe

class WirelessLinkMicrocontrollerTest {
    @Test(timeout = 10000)
    fun reset() {
        val expectedSend = byteArrayOf(0xAA.toByte(), 0x00, 0x00, 0x7A, 0x5D)
        val expectedRecv = byteArrayOf(0xAA.toByte(), 0x00, 0x00, 0x7A, 0x5D)

        var recvCallCount = 0
        val recv = { expectedRecv[recvCallCount++] }
        val sent = mutableListOf<Byte>()
        val microcontroller = WirelessLinkMicrocontroller(
            ReentrantLock(), recv, { bytes -> bytes.forEach { sent.add(it) } })

        val message = microcontroller.reset()
        CollectionAssertionSession(sent).shouldBe(expectedSend.asIterable())
        CollectionAssertionSession(message.bytes.asIterable()).shouldBe(expectedRecv.asIterable())
    }

    @Test(timeout = 10000)
    fun receive() {
        val expectedSend = byteArrayOf(0xAA.toByte(), 0x03, 0x00, 0x2F, 0x0E)
        val expectedRecv = byteArrayOf(0xAA.toByte(), 0x03) + ByteArray(64, { 0x33 }) + byteArrayOf(0x06, 0x9F.toByte())

        var recvCallCount = 0
        val recv = { expectedRecv[recvCallCount++] }
        val sent = mutableListOf<Byte>()
        val microcontroller = WirelessLinkMicrocontroller(
            ReentrantLock(), recv, { bytes -> bytes.forEach { sent.add(it) } })

        val message = microcontroller.receive()
        CollectionAssertionSession(sent).shouldBe(expectedSend.asIterable())
        CollectionAssertionSession(message.bytes.asIterable()).shouldBe(expectedRecv.asIterable())
    }

    @Test(timeout = 10000)
    fun send() {
        val expectedSend = byteArrayOf(0xAA.toByte(), 0x04) + ByteArray(61, { 0x42 }) + byteArrayOf(0xE2.toByte(), 0x7F)
        val expectedRecv = byteArrayOf(0xAA.toByte(), 0x04, 0x01, 0xA6.toByte(), 0xB8.toByte())

        var recvCallCount = 0
        val recv = { expectedRecv[recvCallCount++] }
        val sent = mutableListOf<Byte>()
        val microcontroller = WirelessLinkMicrocontroller(
            ReentrantLock(), recv, { bytes -> bytes.forEach { sent.add(it) } })

        val message = microcontroller.send(ByteArray(61, { 0x42 }))
        CollectionAssertionSession(sent).shouldBe(expectedSend.asIterable())
        CollectionAssertionSession(message.bytes.asIterable()).shouldBe(expectedRecv.asIterable())
    }
}