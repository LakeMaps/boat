package gps

data class GpsActiveSatellites(
    val mode1: Char,
    val mode2: Char,
    val satellitesUsed: GpsChannelArray,
    val dilutionOfPrecision: GpsDilutionOfPrecision
) {
    companion object {
        const val AUTOMATIC_MODE = 'A'
        const val FIX_NOT_AVAILABLE = '1'
        const val MANUAL_MODE = 'M'
        const val MODE_2D = '2'
        const val MODE_3D = '3'
    }
}
