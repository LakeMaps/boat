package io

interface I2CBus {
    fun writeBytes(deviceAddress: Int, address: Int, bytes: ByteArray)
}
