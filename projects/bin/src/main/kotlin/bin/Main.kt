@file:JvmName("Main")

package bin

import core.Boat
import core.PropulsionSystem
import core.broadcast.*
import core.values.GpsValue
import core.values.ValueSerializer
import gps.Gps
import gps.PMTK
import microcontrollers.PropulsionMicrocontroller

import java.net.DatagramSocket
import java.net.InetAddress
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

import rx.Observable
import rx.broadcast.BasicOrder
import rx.broadcast.InMemoryBroadcast
import rx.broadcast.UdpBroadcast
import rx.schedulers.Schedulers

const val ENVIRONMENT_VARIABLE_PREFIX = "LM_"
const val STARTUP_MESSAGE = "Lake Maps NL boat control software started"

fun panic(message: String): Nothing = throw RuntimeException(message)
fun panic(message: String, cause: Throwable): Nothing = throw RuntimeException(message, cause)

fun envPrefixed(name: String) = ENVIRONMENT_VARIABLE_PREFIX + name
fun env(name: String): String = (envPrefixed(name)).let { System.getenv(it) ?: panic("$it must be set") }
fun env(name: String, default: String): String = System.getenv().getOrDefault(envPrefixed(name), default)

fun main(args: Array<String>) {
    println("Lake Maps NL boat control software starting...")

    val propulsionMicrocontroller = SerialPort(env("PROPULSION_SERIAL_DEVICE"), baudRate = 57600).let {
        serialPort -> PropulsionMicrocontroller(serialPort::recvByte, serialPort::send)
    }

    val broadcastAddress = InetAddress.getByName(env("BROADCAST_ADDRESS"))
    val broadcastPort = env("BROADCAST_PORT").toInt()
    val broadcastSocket = DatagramSocket(broadcastPort)
    val broadcast = UdpBroadcast(broadcastSocket, broadcastAddress, broadcastPort, ValueSerializer(), BasicOrder())

    val memoryBroadcast = InMemoryBroadcast()

    val gpsEnabled = !env("DISABLE_GPS", "false").toBoolean()
    if (gpsEnabled) {
        with(SerialPort(env("GPS_SERIAL_DEVICE"), baudRate = 9600)) {
            val gps = Gps({ recvChar() }, { send(it) }, { _ -> })
            gps.setNmeaBaudRate(PMTK.BaudRate.BAUD_RATE_57600)
            disconnect()
        }

        val gps = SerialPort(env("GPS_SERIAL_DEVICE"), baudRate = 57600).let {
            serialPort -> Gps(serialPort::recvChar, serialPort::send, memoryBroadcast::sendAsync)
        }
        gps.setNmeaUpdateRate(PMTK.UpdateRate(100))
        gps.poll()

        Observable.interval(100, TimeUnit.MILLISECONDS)
            .observeOn(Schedulers.io())
            .subscribe({
                // TODO: issue #67
                gps.poll()
            }, {
                e -> panic("Poll GPS timer failed", e)
            })
    }

    val boat = Boat(memoryBroadcast, PropulsionSystem(propulsionMicrocontroller))
    Runtime.getRuntime().addShutdownHook(Thread(boat::shutdown))
    boat.start(io = Schedulers.io(), clock = Schedulers.computation())

    // GPS values should be sent across the wire
    memoryBroadcast.valuesOfType<GpsValue>()
        .observeOn(Schedulers.io())
        .subscribe(broadcast::sendAsync, { e -> panic("Rebroadcast GPS value failed", e) })

    // Anything sent across the wire should be re-broadcasted locally
    broadcast.valuesOfType<Any>().subscribe(memoryBroadcast::sendAsync, { e -> panic("Local rebroadcast failed", e) })

    println(STARTUP_MESSAGE)
    CountDownLatch(1).await()
}
