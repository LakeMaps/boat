@file:JvmName("Main")

package cli

import core.Boat
import core.hardware.ScrewPropeller
import core.values.Motion
import log.Log
import microcontrollers.PropulsionMicrocontroller
import microcontrollers.WirelessLinkMicrocontroller

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

import com.fazecast.jSerialComm.SerialPort
import rx.Observable
import rx.broadcast.InMemoryBroadcast
import rx.schedulers.Schedulers

private fun serialPort(name: String, baudRate: Int = 115200): Triple<SerialPort, () -> Byte, (ByteArray) -> Unit> {
    val serialPort = SerialPort.getCommPort(name)
    serialPort.baudRate = baudRate
    serialPort.openPort()
    serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 0, 0)
    val recv: () -> Byte = {
        val buffer = ByteArray(1)
        serialPort.readBytes(buffer, 1)
        buffer.first()
    }
    val send: (ByteArray) -> Unit = { serialPort.writeBytes(it, it.size.toLong()) }
    return Triple(serialPort, recv, send)
}

fun main(args: Array<String>) {
    if (!args.any()) {
        Log.wtf { "Missing filename for wireless and prop serial ports" }
        return
    }

    val (_, wsRecv, wsSend) = serialPort(args[0])
    val wirelessMicrocontroller = WirelessLinkMicrocontroller(ReentrantLock(), wsRecv, wsSend)
    val (_, psRecv, psSend) = serialPort(args[1])
    val propulsionMicrocontroller = PropulsionMicrocontroller(ReentrantLock(), psRecv, psSend)

    val broadcast = InMemoryBroadcast()
    val props = Pair(ScrewPropeller(propulsionMicrocontroller, 0), ScrewPropeller(propulsionMicrocontroller, 1))
    val boat = Boat(broadcast, props)

    Runtime.getRuntime().addShutdownHook(Thread({ boat.shutdown() }))
    boat.start(io = Schedulers.io(), clock = Schedulers.computation())

    val payloads = Observable.interval(10, TimeUnit.MILLISECONDS)
        .observeOn(Schedulers.io())
        .map { wirelessMicrocontroller.receive() }

    val motions = payloads
        .observeOn(Schedulers.computation())
        .filter { it.containsMessage }
        .doOnNext { Log.d { "RSSI\t${it.rssi}" } }
        .map { Motion.decode(it.body) }

    motions.subscribe { motion ->
        Log.d { "Broadcasting $motion" }
        broadcast.send(motion).subscribe()
    }

    CountDownLatch(1).await()
}
