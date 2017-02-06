package microcontrollers

import java.nio.ByteBuffer
import java.util.Arrays
import java.util.concurrent.locks.Lock

internal interface Microcontroller {
    val payloadSizes: Map<Byte, Int>

    val lock: Lock

    val recv: () -> Byte
    val send: (ByteArray) -> Unit

    fun writeMessage(m: Message): Message {
        return sync {
            send(m.bytes)
            return@sync readMessage()
        }
    }

    private fun readMessage(): Message {
        var state: MessageState = MessageState.Started()
        var buffer = ByteBuffer.allocate(128)
        while (true) {
            state = when (state) {
                is MessageState.Started -> state.next(recv, { b -> buffer.put(b) })
                is MessageState.Command -> state.next(recv, { b -> buffer.put(b) }, payloadSizes)
                is MessageState.Payload -> state.next(recv, { b -> buffer.put(b) })
                is MessageState.Checksum -> {
                    val bytes = buffer.array().sliceArray(0 until buffer.position())
                    val message = Message(bytes[1], bytes.sliceArray(2..bytes.lastIndex))
                    val nextTwoBytes = byteArrayOf(recv(), recv())

                    buffer.clear()

                    if (Arrays.equals(nextTwoBytes, message.checksum)) {
                        return message
                    }

                    // Message is corrupt
                    MessageState.Started()
                }
            }
        }
    }

    private fun <T> sync(f: () -> T): T {
        lock.lock()
        try {
            return f()
        }
        finally {
            lock.unlock()
        }
    }
}
