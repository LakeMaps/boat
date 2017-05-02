package gps

import units.Angle
import units.Knot
import units.Degree
import units.Quantity
import units.Speed

/**
 * An NMEA 0183 `VTG` sentence.
 *
 * @property course the measured heading
 * @property speed the measured speed
 * @property mode the device mode when this velocity was measured (i.e. [MODE_AUTONOMOUS], [MODE_DIFFERENTIAL], or [MODE_ESTIMATED])
 */
data class GpsGroundVelocity(val course: Quantity<Angle, Degree>, val speed: Quantity<Speed, Knot>, val mode: Char) {
    companion object {
        const val MODE_AUTONOMOUS = 'A'
        const val MODE_DIFFERENTIAL = 'D'
        const val MODE_ESTIMATED = 'E'
    }
}
