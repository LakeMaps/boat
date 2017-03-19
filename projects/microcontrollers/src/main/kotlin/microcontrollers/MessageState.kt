package microcontrollers

import log.Log

internal sealed class MessageState {
    class Started : MessageState() {
        fun next(recv: () -> Byte, buffer: (Byte) -> Unit): MessageState {
            val byte = recv()

            if (byte == Message.MESSAGE_START) {
                buffer(byte)
                return MessageState.Command()
            } else {
                Log.d { "Skipping 0x${byte.hex()} in search of message start byte" }
                return MessageState.Started()
            }
        }
    }
    class Command : MessageState() {
        fun next(recv: () -> Byte, buffer: (Byte) -> Unit, payloadSizes: Map<Byte, Int>): MessageState {
            val command = recv()
            buffer(command)

            if (!payloadSizes.containsKey(command)) {
                Log.w { "Received Invalid command byte 0x${command.hex()}" }
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
