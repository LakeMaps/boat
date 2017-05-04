package core.control

class PIDController<in T>(
    private val setpoint: T,
    private val dt: Long,
    private val gains: Gains,
    private val e: (T, T) -> Double
) {
    data class Gains(val kp: Double, val ki: Double, val kd: Double)

    companion object {
        const val BUFFER_SIZE = 4
    }

    private val errors = ErrorState(BUFFER_SIZE)

    init {
        // To simplify calculations, let's pre-fill the buffer. This allows
        // us to not have to worry about the state where the returned array
        // doesn't have enough elements to calculate a delta (e.g. zero or one items).
        (0..BUFFER_SIZE).forEach { errors.add(0.0) }
    }

    fun add(value: T) = errors.add(e(setpoint, value))

    fun nextOutput(): Double {
        val (kp, ki, kd) = gains
        val integral = errors.sum({ it * dt })
        val de = errors.nth(-1) - errors.last()
        val derivative = de / dt
        return (kp * errors.last()) + (ki * integral) + (kd * derivative)
    }
}
