package gps

import java.time.OffsetDateTime

data class GpsNavInfo(
    val instant: OffsetDateTime,
    val valid: Boolean,
    val position: GpsPosition,
    val speed: Double,
    val course: Double,
    val mode: Char
) {
    companion object {
        const val STATUS_VALID = 'A'
        const val MODE_AUTONOMOUS = 'A'
        const val MODE_DIFFERENTIAL = 'D'
        const val MODE_ESTIMATED = 'E'
    }
}
