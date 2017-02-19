package gps

import java.util.Arrays

class GpsChannelArray(val channels: IntArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other?.let { it::class.java } != this::class.java) {
            return false
        }

        return Arrays.equals(channels, (other as GpsChannelArray).channels)
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(channels)
    }
}
