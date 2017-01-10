package microcontrollers

internal sealed class MessageState {
    class Started : MessageState() {
        fun next(recv: () -> Byte, buffer: (Byte) -> Unit): MessageState {
            val byte = recv()

            if (byte == Message.MESSAGE_START) {
                buffer(byte)
                return MessageState.Command()
            } else {
                // This isn't the byte we're looking for
                return MessageState.Started()
            }
        }
    }
    class Command : MessageState() {
        fun next(recv: () -> Byte, buffer: (Byte) -> Unit, payloadSizes: Map<Byte, Int>): MessageState {
            val command = recv()
            buffer(command)

            if (!payloadSizes.containsKey(command)) {
                // Invalid command received
                return MessageState.Started()
            } else {
                val payloadSize = payloadSizes[command]!!
                return MessageState.Payload(payloadSize)
            }
        }
    }
    class Payload(val payloadSize: Int) : MessageState() {
        fun next(recv: () -> Byte, buffer: (Byte) -> Unit): MessageState {
            for (i in 0..(payloadSize - 1)) {
                buffer(recv())
            }
            return MessageState.Checksum()
        }
    }
    class Checksum : MessageState()
}
