@file:Suppress("DEPRECATION")

package microcontrollers

import org.junit.Test
import kotlin.test.CollectionAssertionSession
import kotlin.test.shouldBe

class MessageTest {
    @Test
    fun messageBytesAreCorrect() {
        val message = Message(0x10, byteArrayOf(0x00))
        val expected = byteArrayOf(0xAA.toByte(), 0x10, 0x00, 0x79, 0x2E)

        CollectionAssertionSession(message.bytes.asIterable()).shouldBe(expected.asIterable())
    }
}
