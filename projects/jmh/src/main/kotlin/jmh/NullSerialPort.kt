package jmh

import org.openjdk.jmh.infra.Blackhole

class NullSerialPort(private val blackhole: Blackhole, private val bytes: ByteArray) {
    private var count: Int = 0

    fun recv() = bytes[count++]
    fun send(bytes: ByteArray) = bytes.forEach(blackhole::consume)
}
