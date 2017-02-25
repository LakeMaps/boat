package gps

/**
 * A NMEA 0183 `GSA` sentence containing GNSS DOP and active satellites.
 *
 * @property mode1 whether the device is in [MANUAL_MODE] or [AUTOMATIC_MODE]
 * @property mode2 whether the device has a fix or is [MODE_2D] or [MODE_3D]
 * @property satellitesUsed an array of the satellite IDs on channels 1 through 12
 * @property dilutionOfPrecision the dilution of precision
 * @constructor Constructs a GSA sentence with the given properties
 */
data class GpsActiveSatellites(
    val mode1: Char,
    val mode2: Char,
    val satellitesUsed: GpsChannelArray?,
    val dilutionOfPrecision: GpsDilutionOfPrecision?
) {
    companion object {
        const val AUTOMATIC_MODE = 'A'
        const val FIX_NOT_AVAILABLE = '1'
        const val MANUAL_MODE = 'M'
        const val MODE_2D = '2'
        const val MODE_3D = '3'
    }
}
