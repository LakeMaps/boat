@file:Suppress("DEPRECATION")

package microcontrollers

import kotlin.test.CollectionAssertionSession
import kotlin.test.shouldBe

import org.junit.Test

class MessageTest {
    @Test
    fun messageBytesAreCorrect() {
        val message = Message(0x10, byteArrayOf(0x00))
        val expected = byteArrayOf(0xAA.toByte(), 0x10, 0x00, 0x79, 0x2E)

        CollectionAssertionSession(message.bytes.asIterable()).shouldBe(expected.asIterable())
    }

    @Test
    fun messageChecksumBytesAreCorrect() {
        val message = Message(0x10, byteArrayOf(0x00))
        val expected = byteArrayOf(0x79, 0x2E)

        CollectionAssertionSession(message.checksum.asIterable()).shouldBe(expected.asIterable())
    }
}
