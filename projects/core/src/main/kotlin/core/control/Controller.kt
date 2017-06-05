package core.control

interface Controller {
    fun addError(value: Double, dtMs: Long)

    fun nextOutput(): Double
}
