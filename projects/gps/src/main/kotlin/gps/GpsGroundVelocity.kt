package gps

data class GpsGroundVelocity(val course: Double, val speed: Double, val mode: Char) {
    companion object {
        const val MODE_AUTONOMOUS = 'A'
        const val MODE_DIFFERENTIAL = 'D'
        const val MODE_ESTIMATED = 'E'
    }
}
