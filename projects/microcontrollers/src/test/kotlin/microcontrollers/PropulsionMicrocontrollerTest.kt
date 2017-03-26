@file:Suppress("DEPRECATION")

package microcontrollers

import kotlin.test.CollectionAssertionSession
import kotlin.test.shouldBe

import java.util.concurrent.locks.ReentrantLock

import org.junit.Test

class PropulsionMicrocontrollerTest {
    @Test(timeout = 10000)
    fun reset() {
        val expectedSend = byteArrayOf(0xAA.toByte(), 0x10, 0x00, 0x79, 0x2E)
        val expectedRecv = byteArrayOf(0xAA.toByte(), 0x10, 0x00, 0x79, 0x2E)

        var recvCallCount = 0
        val recv = { expectedRecv[recvCallCount++] }
        val sent = mutableListOf<Byte>()
        val microcontroller = PropulsionMicrocontroller(
            ReentrantLock(), recv, { bytes -> bytes.forEach { sent.add(it) } })

        microcontroller.reset()
        CollectionAssertionSession(sent).shouldBe(expectedSend.asIterable())
    }

    @Test(timeout = 10000)
    fun setSpeed() {
        val expectedSend = byteArrayOf(0xAA.toByte(), 0x13, 0x00, 0x80.toByte(), 0x00, 0x00, 0xB6.toByte(), 0xF8.toByte())
        val expectedRecv = byteArrayOf(0xAA.toByte(), 0x13, 0x00, 0x80.toByte(), 0x00, 0x00, 0xB6.toByte(), 0xF8.toByte())

        var recvCallCount = 0
        val recv = { expectedRecv[recvCallCount++] }
        val sent = mutableListOf<Byte>()
        val microcontroller = PropulsionMicrocontroller(
            ReentrantLock(), recv, { bytes -> bytes.forEach { sent.add(it) } })

        microcontroller.setSpeed(128, 0)
        CollectionAssertionSession(sent).shouldBe(expectedSend.asIterable())
    }
}
