package jmh

import core.Boat
import core.broadcast.Broadcast
import core.hardware.Propeller
import core.hardware.ScrewPropeller
import core.values.Motion
import microcontrollers.PropulsionMicrocontroller
import microcontrollers.WirelessLinkMicrocontroller

import java.util.concurrent.CountDownLatch
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
import rx.Observable
import rx.broadcast.InMemoryBroadcast
import rx.schedulers.Schedulers
import rx.schedulers.TestScheduler

@Fork(1)
@Measurement(iterations = 20)
@Warmup(iterations = 0)
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
open class BoatBenchmark {
    var surge = Math.PI

    var yaw = Math.PI

    @Benchmark
    fun tick(): Pair<NullPropeller, NullPropeller> {
        val broadcast = Broadcast(InMemoryBroadcast())
        val scheduler = TestScheduler()
        val props = Pair(NullPropeller(), NullPropeller())
        val boat = Boat(broadcast, props)

        boat.start(io = scheduler, clock = scheduler)

        broadcast.send(Motion(surge, yaw)).subscribe()
        scheduler.advanceTimeBy(boat.SLEEP_DURATION_MS, TimeUnit.MILLISECONDS)

        return Pair(props.first, props.second)
    }

    class Foo(private val prop: Propeller, private val callback: (Double) -> Unit): Propeller {
        override var speed: Double
            get() = prop.speed
            set(value) {
                prop.speed = value
                callback(prop.speed)
            }
    }

    @Benchmark
    fun core(blackhole: Blackhole) {
        val counter = CountDownLatch(4)

        val motion1 = byteArrayOf(
            0x09.toByte(), 0xe1.toByte(), 0x7a.toByte(),
            0x14.toByte(), 0xae.toByte(), 0x47.toByte(),
            0xe1.toByte(), 0xda.toByte(), 0x3f.toByte(),
            0x11.toByte(), 0xb8.toByte(), 0x1e.toByte(),
            0x85.toByte(), 0xeb.toByte(), 0x51.toByte(),
            0xb8.toByte(), 0xce.toByte(), 0x3f.toByte())
        val motion2 = byteArrayOf(
            0x09.toByte(), 0xb8.toByte(), 0x1e.toByte(),
            0x85.toByte(), 0xeb.toByte(), 0x51.toByte(),
            0xb8.toByte(), 0xce.toByte(), 0x3f.toByte(),
            0x11.toByte(), 0xe1.toByte(), 0x7a.toByte(),
            0x14.toByte(), 0xae.toByte(), 0x47.toByte(),
            0xe1.toByte(), 0xda.toByte(), 0x3f.toByte())
        val wsSerialPort = NullSerialPort(blackhole,
            // Bytes for the 1st receive
            byteArrayOf(0xAA.toByte(), 0x03, 0x01) + motion1 + ByteArray(45, { 0 }) + byteArrayOf(0xFF.toByte(), 0x13.toByte()) +
            // Bytes for the 2nd receive
            byteArrayOf(0xAA.toByte(), 0x03, 0x01) + motion2 + ByteArray(45, { 0 }) + byteArrayOf(0xD5.toByte(), 0xdb.toByte())
        )
        val psSerialPort = NullSerialPort(blackhole, byteArrayOf(
            // 1st response from both
            0xAA.toByte(), 0x13, 0x00, 0x80.toByte(), 0x00, 0x00, 0xB6.toByte(), 0xF8.toByte(),
            0xAA.toByte(), 0x13, 0x00, 0x80.toByte(), 0x00, 0x00, 0xB6.toByte(), 0xF8.toByte(),
            // 2nd response from both
            0xAA.toByte(), 0x13, 0x00, 0x80.toByte(), 0x00, 0x00, 0xB6.toByte(), 0xF8.toByte(),
            0xAA.toByte(), 0x13, 0x00, 0x80.toByte(), 0x00, 0x00, 0xB6.toByte(), 0xF8.toByte(),
            // Boat#shutdown zeros the props
            0xAA.toByte(), 0x13, 0x00, 0x80.toByte(), 0x00, 0x00, 0xB6.toByte(), 0xF8.toByte(),
            0xAA.toByte(), 0x13, 0x00, 0x80.toByte(), 0x00, 0x00, 0xB6.toByte(), 0xF8.toByte()
        ))
        val wirelessMicrocontroller = WirelessLinkMicrocontroller(ReentrantLock(), wsSerialPort::recv, wsSerialPort::send)
        val propulsionMicrocontroller = PropulsionMicrocontroller(ReentrantLock(), psSerialPort::recv, psSerialPort::send)

        val props = Pair(
            Foo(ScrewPropeller(propulsionMicrocontroller, 0), { counter.countDown() }),
            Foo(ScrewPropeller(propulsionMicrocontroller, 1), { counter.countDown() }))

        val boatClockScheduler = TestScheduler()
        val wirelessClockScheduler = TestScheduler()
        val broadcast = Broadcast(InMemoryBroadcast())
        val boat = Boat(broadcast, props)

        boat.start(io = Schedulers.io(), clock = boatClockScheduler)

        val payloads = Observable.interval(1, TimeUnit.SECONDS, wirelessClockScheduler)
            .map { wirelessMicrocontroller.receive() }

        val motions = payloads
            .filter { it.containsMessage }
            .map { Motion.decode(it.body) }

        motions.subscribe({ broadcast.send(it).subscribe() }, { throw RuntimeException(it) })

        wirelessClockScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        boatClockScheduler.advanceTimeBy(boat.SLEEP_DURATION_MS, TimeUnit.MILLISECONDS)

        wirelessClockScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        boatClockScheduler.advanceTimeBy(boat.SLEEP_DURATION_MS, TimeUnit.MILLISECONDS)

        counter.await()
        boat.shutdown()
    }
}
