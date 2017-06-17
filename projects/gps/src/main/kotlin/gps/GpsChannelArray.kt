package gps

import java.util.Arrays

/**
 * An array of numbers, one per channel.
 *
 * @property channels the array of channel values
 */
class GpsChannelArray(val channels: IntArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other?.javaClass != this.javaClass) {
            return false
        }

        return Arrays.equals(channels, (other as GpsChannelArray).channels)
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(channels)
    }
}
