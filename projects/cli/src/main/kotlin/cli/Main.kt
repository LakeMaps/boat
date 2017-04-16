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

import rx.Observable
import rx.broadcast.InMemoryBroadcast
import rx.schedulers.Schedulers

const val STARTUP_MESSAGE = """
                  ______
          _______/ooo__\\_______
          \                   |]
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    Lake Maps NL boat control software
"""

fun main(args: Array<String>) {
    if (!args.any()) {
        Log.wtf { "Missing filename for wireless and prop serial ports" }
        return
    }

    val broadcast = InMemoryBroadcast()

    val wSerialPort = SerialPort(args[0], baudRate = 57600)
    val pSerialPort = SerialPort(args[1], baudRate = 57600)
    val wirelessMicrocontroller = WirelessLinkMicrocontroller(ReentrantLock(), wSerialPort::recv, wSerialPort::send)
    val propulsionMicrocontroller = PropulsionMicrocontroller(ReentrantLock(), pSerialPort::recv, pSerialPort::send)

    val props = Pair(ScrewPropeller(propulsionMicrocontroller, 0), ScrewPropeller(propulsionMicrocontroller, 1))
    val boat = Boat(broadcast, props)

    Runtime.getRuntime().addShutdownHook(Thread({ boat.shutdown() }))
    boat.start(io = Schedulers.io(), clock = Schedulers.computation())

    val payloads = Observable.interval(boat.SLEEP_DURATION_MS, TimeUnit.MILLISECONDS)
        .observeOn(Schedulers.io())
        .map { wirelessMicrocontroller.receive() }
        .onBackpressureLatest()

    val motions = payloads
        .observeOn(Schedulers.computation())
        .filter { it.containsMessage }
        .doOnNext { Log.d { "RSSI\t${it.rssi}" } }
        .map { Motion.decode(it.body) }

    motions.subscribe { motion ->
        Log.d { "Broadcasting $motion" }
        broadcast.send(motion).subscribe()
    }

    Log.d { STARTUP_MESSAGE }
    CountDownLatch(1).await()
}
