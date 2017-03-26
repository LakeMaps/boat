package jmh

import microcontrollers.Message
import microcontrollers.PropulsionMicrocontroller
import microcontrollers.WirelessLinkMicrocontroller
import microcontrollers.WirelessLinkReceiveMessage

import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Warmup
import org.openjdk.jmh.infra.Blackhole

@Fork(1)
@Measurement(iterations = 20)
@Warmup(iterations = 0)
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
open class MicrocontrollerBenchmark {
    @Benchmark
    fun resetPropulsionMicrocontroller(blackhole: Blackhole): Message {
        val serial = NullSerialPort(blackhole, byteArrayOf(0xAA.toByte(), 0x10, 0x00, 0x79, 0x2E))
        val microcontroller = PropulsionMicrocontroller(ReentrantLock(), serial::recv, serial::send)
        return microcontroller.reset()
    }

    @Benchmark
    fun setSpeedPropulsionMicrocontroller(blackhole: Blackhole): Message {
        val serial = NullSerialPort(blackhole, byteArrayOf(0xAA.toByte(), 0x13, 0x00, 0x80.toByte(), 0x00, 0x00, 0xB6.toByte(), 0xF8.toByte()))
        val microcontroller = PropulsionMicrocontroller(ReentrantLock(), serial::recv, serial::send)
        return microcontroller.setSpeed(128, 0)
    }

    @Benchmark
    fun resetWirelessLinkMicrocontroller(blackhole: Blackhole): Message {
        val serial = NullSerialPort(blackhole, byteArrayOf(0xAA.toByte(), 0x00, 0x00, 0x7A, 0x5D))
        val microcontroller = WirelessLinkMicrocontroller(ReentrantLock(), serial::recv, serial::send)
        return microcontroller.reset()
    }

    @Benchmark
    fun receiveWirelessLinkMicrocontroller(blackhole: Blackhole): WirelessLinkReceiveMessage {
        val serial = NullSerialPort(blackhole, byteArrayOf(0xAA.toByte(), 0x03) + ByteArray(64, { 0x33 }) + byteArrayOf(0x06, 0x9F.toByte()))
        val microcontroller = WirelessLinkMicrocontroller(ReentrantLock(), serial::recv, serial::send)
        return microcontroller.receive()
    }

    @Benchmark
    fun sendWirelessLinkMicrocontroller(blackhole: Blackhole): Message {
        val serial = NullSerialPort(blackhole, byteArrayOf(0xAA.toByte(), 0x04, 0x01, 0xA6.toByte(), 0xB8.toByte()))
        val microcontroller = WirelessLinkMicrocontroller(ReentrantLock(), serial::recv, serial::send)
        return microcontroller.send(ByteArray(61, { 0x42 }))
    }
}
