package cli

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.Arrays
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

internal class Datagram(address: String, port: Int) {
    companion object {
        const val MAX_UDP_PACKET_SIZE = 65507
    }

    private val address = InetAddress.getByName(address)

    private val socket: DatagramSocket = DatagramSocket(port)

    private val lock: Lock = ReentrantLock()

    fun recv(): ByteArray {
        val buffer = ByteArray(MAX_UDP_PACKET_SIZE)
        val packet = DatagramPacket(buffer, buffer.size)
        socket.receive(packet);
        return Arrays.copyOf(buffer, packet.length)
    }

    fun send(bytes: ByteArray) = socket.send(DatagramPacket(bytes, bytes.size, address, socket.localPort))
}
