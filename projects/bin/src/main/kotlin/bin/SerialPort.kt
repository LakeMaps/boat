package bin

internal data class SerialPort(val name: String, val baudRate: Int) {
    private val serialPort = com.fazecast.jSerialComm.SerialPort.getCommPort(name)!!

    private val readBuffer = ByteArray(1)

    init {
        serialPort.baudRate = baudRate
        serialPort.openPort()
        serialPort.setComPortTimeouts(com.fazecast.jSerialComm.SerialPort.TIMEOUT_READ_BLOCKING, 0, 0)
    }

    fun recvByte(): Byte {
        serialPort.readBytes(readBuffer, 1)
        return readBuffer.first()
    }

    fun recvChar(): Char = recvByte().toChar()

    fun send(bytes: ByteArray) {
        serialPort.writeBytes(bytes, bytes.size.toLong())
    }

    fun disconnect() {
        serialPort.outputStream.flush()
        serialPort.closePort()
    }
}
