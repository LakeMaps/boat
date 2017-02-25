package microcontrollers

import org.junit.Assert
import org.junit.Test

class WirelessLinkReceiveMessageTest {
    @Test
    fun testMessageContainsMessage() {
        val message = WirelessLinkReceiveMessage(
            Message(0x03, byteArrayOf(0x01) + ByteArray(61, { 42 }) + byteArrayOf(0x00, 0x72)))

        Assert.assertTrue("Message.containsMessage should be true", message.containsMessage)
    }

    @Test
    fun testMessageBodyDoesContainTheCorrectBytes() {
        val message = WirelessLinkReceiveMessage(
            Message(0x03, byteArrayOf(0x01) + ByteArray(61, { 42 }) + byteArrayOf(0x00, 0x72)))

        Assert.assertArrayEquals(ByteArray(61, { 42 }), message.body)
    }

    @Test
    fun testMessageRssiIsValid() {
        val message = WirelessLinkReceiveMessage(
            Message(0x03, byteArrayOf(0x01) + ByteArray(61, { 42 }) + byteArrayOf(0x00, 0x72)))

        Assert.assertEquals(114, message.rssi)
    }
}
