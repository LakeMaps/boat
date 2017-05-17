@file:JvmName("Main")

package cli

import core.Boat
import core.PropulsionSystem
import core.broadcast.Broadcast
import core.values.GpsValue
import core.values.Motion
import gps.Gps
import log.Log
import microcontrollers.PropulsionMicrocontroller

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

import com.google.protobuf.InvalidProtocolBufferException
import rx.Observable
import rx.Subscriber
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
        Log.wtf { "Missing filename for wireless address, and prop and GPS serial ports" }
        return
    }

    val broadcast = Broadcast(InMemoryBroadcast())

    val socket = Datagram(args[0], 12345)
    val pSerialPort = SerialPort(args[1], baudRate = 57600)
    val gSerialPort = SerialPort(args[2], baudRate =  9600)
    val propulsionMicrocontroller = PropulsionMicrocontroller(ReentrantLock(), pSerialPort::recv, pSerialPort::send)
    val gpsMicrocontroller = Gps({ gSerialPort.recv().toChar() }, { msg -> broadcast.send(msg).subscribe() })

    val boat = Boat(broadcast, PropulsionSystem(propulsionMicrocontroller))

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

    broadcast.valuesOfType<GpsValue>()
        .observeOn(Schedulers.io())
        .subscribe {
            Log.d {"Sending ${it.encode().size} bytes across the wire"}
            socket.send(it.encode())
        }

    val payloads = Observable.create({
        subscriber: Subscriber<in ByteArray> ->
            while (true) {
                val bytes = socket.recv()
                subscriber.onNext(bytes)
            }
        })
        .subscribeOn(Schedulers.io())
        .onBackpressureDrop()

    val motions = payloads
        .observeOn(Schedulers.computation())
        .map { try {
            Motion.decode(it)
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
