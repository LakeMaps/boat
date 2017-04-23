@file:JvmName("Main")

package cli

import com.google.protobuf.InvalidProtocolBufferException
import core.Boat
import core.hardware.ScrewPropeller
import core.values.GpsValue
import core.values.Motion
import gps.Gps
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
        Log.wtf { "Missing filename for wireless and prop and GPS serial ports" }
        return
    }

    val broadcast = InMemoryBroadcast()

    val wSerialPort = SerialPort(args[0], baudRate = 57600)
    val pSerialPort = SerialPort(args[1], baudRate = 57600)
    val gSerialPort = SerialPort(args[2], baudRate =  9600)
    val wirelessMicrocontroller = WirelessLinkMicrocontroller(ReentrantLock(), wSerialPort::recv, wSerialPort::send)
    val propulsionMicrocontroller = PropulsionMicrocontroller(ReentrantLock(), pSerialPort::recv, pSerialPort::send)
    val gpsMicrocontroller = Gps({ gSerialPort.recv().toChar() }, { msg -> broadcast.send(msg).subscribe() })

    val props = Pair(ScrewPropeller(propulsionMicrocontroller, 0), ScrewPropeller(propulsionMicrocontroller, 1))
    val boat = Boat(broadcast, props)

    Runtime.getRuntime().addShutdownHook(Thread({ boat.shutdown() }))
    boat.start(io = Schedulers.io(), clock = Schedulers.computation())

    Observable.interval(500, TimeUnit.MILLISECONDS)
        .observeOn(Schedulers.io())
        .subscribe {
            try {
                gpsMicrocontroller.poll()
            } catch (e: Exception) {
                Log.w { "$e" }
                /* TODO: issue #67 */
            }
        }

    broadcast.valuesOfType(GpsValue::class.java)
        .observeOn(Schedulers.io())
        .subscribe {
            Log.d {"Sending ${it.encode().size} bytes across the wire"}
            wirelessMicrocontroller.send(it.encode())
        }

    val payloads = Observable.interval(boat.SLEEP_DURATION_MS, TimeUnit.MILLISECONDS)
        .observeOn(Schedulers.io())
        .map { wirelessMicrocontroller.receive() }
        .onBackpressureLatest()

    val motions = payloads
        .observeOn(Schedulers.computation())
        .filter { it.containsMessage }
        .doOnNext { Log.d { "RSSI\t${it.rssi}" } }
        .map { try {
            Motion.decode(it.body)
        } catch (e: InvalidProtocolBufferException) {
            Log.w { "$e" }
            null
        } }
        .filter { it != null }
        .map { it!! }

    motions.subscribe { motion ->
        Log.d { "Broadcasting $motion" }
        broadcast.send(motion).subscribe()
    }

    Log.d { STARTUP_MESSAGE }
    CountDownLatch(1).await()
}
