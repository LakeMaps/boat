package gps

import units.Angle
import units.Milliknot
import units.Nanodegree
import units.Quantity
import units.Speed
import java.time.OffsetDateTime

/**
 * An NMEA 0183 `RMC` sentence.
 *
 * @property instant the timestamp for this measurement
 * @property valid whether or not this position is valid
 * @property position the GPS position
 * @property speed the measured speed
 * @property course the measured heading
 * @property mode the device mode when this velocity was measured (i.e. [MODE_AUTONOMOUS], [MODE_DIFFERENTIAL], or [MODE_ESTIMATED])
 */
data class GpsNavInfo(
    val instant: OffsetDateTime,
    val valid: Boolean,
    val position: GpsPosition?,
    val speed: Quantity<Speed, Milliknot>?,
    val course: Quantity<Angle, Nanodegree>?,
    val mode: Char
) {
    companion object {
        const val STATUS_VALID = 'A'
        const val MODE_AUTONOMOUS = 'A'
        const val MODE_DIFFERENTIAL = 'D'
        const val MODE_ESTIMATED = 'E'
    }
}
