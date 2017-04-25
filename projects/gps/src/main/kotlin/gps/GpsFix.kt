package gps

import java.time.OffsetTime
import javax.measure.Quantity
import javax.measure.quantity.Angle
import javax.measure.quantity.Length

/**
 * An NMEA 0183 GGA sentence.
 *
 * @property time UTC time of this position
 * @property position the GPS position
 * @property positionFixIndicator whether the device does ([GPS_FIX] or [DIFFERENTIAL_GPS_FIX]) or does not have a fix ([FIX_NOT_AVAILABLE])
 * @property satellitesUsed the number of satellites used in this position (from 0 to 14)
 * @property dilutionOfPrecision the dilution of precision
 * @property altitude antenna altitude above or below mean-sea-level
 * @property geoidalSeparation geoidal separation
 */
data class GpsFix(
    val time: OffsetTime,
    val position: GpsPosition?,
    val positionFixIndicator: Char,
    val satellitesUsed: Int,
    val dilutionOfPrecision: GpsDilutionOfPrecision?,
    val altitude: Quantity<Length>?,
    val geoidalSeparation: Quantity<Length>?
) {
    companion object {
        const val FIX_NOT_AVAILABLE = '0'
        const val GPS_FIX = '1'
        const val DIFFERENTIAL_GPS_FIX = '2'
    }
}
