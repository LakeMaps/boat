package cli

internal class SerialPort(private val name: String, val baudRate: Int) {
    private val serialPort = com.fazecast.jSerialComm.SerialPort.getCommPort(name)!!

    private val readBuffer = ByteArray(1)

    init {
        serialPort.baudRate = baudRate
        serialPort.openPort()
        serialPort.setComPortTimeouts(com.fazecast.jSerialComm.SerialPort.TIMEOUT_READ_BLOCKING, 0, 0)
    }

    fun recv(): Byte {
        serialPort.readBytes(readBuffer, 1)
        return readBuffer.first()
    }

    fun send(bytes: ByteArray) {
        serialPort.writeBytes(bytes, bytes.size.toLong())
    }
}
