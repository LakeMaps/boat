package core.control

class ControlSystem<in T>(private val controller: Controller, private val e: (T, T) -> Double) {
    fun addValue(setpoint: T, value: T, dtMs: Long) = controller.addError(e(setpoint, value), dtMs)

    fun nextOutput() = controller.nextOutput()
}
