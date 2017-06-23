@file:JvmName("Main")

package cli

import core.Boat
import core.PropulsionSystem
import core.broadcast.*
import core.values.GpsValue
import core.values.ValueSerializer
import gps.Gps
import gps.PMTK
import log.Log
import microcontrollers.PropulsionMicrocontroller

import java.net.DatagramSocket
import java.net.InetAddress
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

import rx.Observable
import rx.broadcast.BasicOrder
import rx.broadcast.InMemoryBroadcast
import rx.broadcast.UdpBroadcast
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

    val broadcast = InMemoryBroadcast()
    val port = 12345
    val udpBroadcast = UdpBroadcast(
        DatagramSocket(port), InetAddress.getByName(args[0]), port, ValueSerializer(), BasicOrder())
    val pSerialPort = SerialPort(args[1], baudRate = 57600)
    var gSerialPort = SerialPort(args[2], baudRate =  9600)
    val propulsionMicrocontroller = PropulsionMicrocontroller(ReentrantLock(), { gSerialPort.recvByte() }, pSerialPort::send)
    val gpsReceiver = Gps({ gSerialPort.recvChar() }, gSerialPort::send, { msg -> broadcast.send(msg).subscribe() })

    val boat = Boat(broadcast, PropulsionSystem(propulsionMicrocontroller))

    gpsReceiver.setNmeaBaudRate(PMTK.BaudRate.BAUD_RATE_57600)
    gpsReceiver.poll()
    gSerialPort.disconnect()
    gSerialPort = gSerialPort.copy(baudRate = 57600)
    gpsReceiver.setNmeaUpdateRate(PMTK.UpdateRate(100))
    gpsReceiver.poll()

    Runtime.getRuntime().addShutdownHook(Thread(boat::shutdown))
    boat.start(io = Schedulers.io(), clock = Schedulers.computation())

    Observable.interval(500, TimeUnit.MILLISECONDS)
        .observeOn(Schedulers.io())
        .subscribe {
            try {
                gpsReceiver.poll()
            } catch (e: Exception) {
                Log.w { "$e" }
                /* TODO: issue #67 */
            }
        }

    broadcast.valuesOfType<GpsValue>()
        .observeOn(Schedulers.io())
        .subscribe { udpBroadcast.send(it).subscribe() }

    udpBroadcast.valuesOfType<Any>()
        .subscribe { broadcast.send(it).subscribe() }

    Log.d { STARTUP_MESSAGE }
    CountDownLatch(1).await()
}
