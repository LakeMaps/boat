package core.control

import org.junit.Assert
import org.junit.Test

class ControlSystemTest {
    companion object {
        const val DELTA = 0.0001
    }

    @Test
    fun zeroValuesProducesZeroOutput() {
        val clamp = { x: Double -> x }
        val e = { a: Double, b: Double -> a - b }
        val controller = PidController(PidController.Gains(kp = 1.0, ki = 1.0, kd = 1.0), clamp)
        val controlSystem = ControlSystem(controller, e)

        Assert.assertEquals(0.0, controlSystem.nextOutput(), DELTA)
    }

    @Test
    fun startingAtTheSetpointProducesZeroOutput() {
        val clamp = { x: Double -> x }
        val e = { a: Double, b: Double -> a - b }
        val setpoint = 10.0
        val controller = PidController(PidController.Gains(kp = 1.0, ki = 1.0, kd = 1.0), clamp)
        val controlSystem = ControlSystem(controller, e)

        controlSystem.addValue(setpoint, 10.0, 1)

        Assert.assertEquals(0.0, controller.nextOutput(), DELTA)
    }

    @Test
    fun startingHalfwayProducesHalfOutput() {
        val clamp = { x: Double -> x }
        val e = { a: Double, b: Double -> a - b }
        val setpoint = 10.0
        val controller = PidController(PidController.Gains(kp = 1.0, ki = 1.0, kd = 1.0), clamp)
        val controlSystem = ControlSystem(controller, e)

        controlSystem.addValue(setpoint, 5.0, 1)

        Assert.assertEquals(5.0, controller.nextOutput(), DELTA)
    }
}
