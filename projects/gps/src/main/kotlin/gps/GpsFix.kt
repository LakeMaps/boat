package gps

import java.time.OffsetTime

data class GpsFix(
    val time: OffsetTime,
    val position: GpsPosition?,
    val positionFixIndicator: Char,
    val satellitesUsed: Int,
    val dilutionOfPrecision: GpsDilutionOfPrecision?,
    val altitude: Double?,
    val geoidalSeparation: Double?
) {
    companion object {
        const val FIX_NOT_AVAILABLE = '0'
        const val GPS_FIX = '1'
        const val DIFFERENTIAL_GPS_FIX = '2'
    }
}
