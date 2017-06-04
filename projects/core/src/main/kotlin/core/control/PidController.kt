package core.control

import core.collections.RingBuffer

class PidController(private var gains: Gains, private val clamp: (Double) -> Double): Controller {
    data class Gains(val kp: Double, val ki: Double, val kd: Double)

    companion object {
        const val BUFFER_SIZE = 4
    }

    private val errors = ErrorState(BUFFER_SIZE)

    private val integralTerms = RingBuffer(BUFFER_SIZE)

    private var derivative = 0.0

    init {
        // To simplify calculations, let's pre-fill the buffer. This allows
        // us to not have to worry about the state where the returned array
        // doesn't have enough elements to calculate a delta (e.g. zero or one items).
        (0..BUFFER_SIZE).forEach { errors.add(0.0) }
    }

    override fun addError(value: Double, dtMs: Long) {
        val (_, ki, _) = gains

        errors.add(value)

        integralTerms.add(clamp(value * dtMs * ki))
        derivative = (errors.last() - errors.nth(-1)) / dtMs
    }

    override fun nextOutput(): Double {
        val (kp, _, kd) = gains
        val integral = integralTerms.array.sum()
        return clamp((kp * errors.last()) + integral + (kd * derivative))
    }

    fun setGains(gains: Gains): PidController {
        this.gains = gains
        return this
    }
}
