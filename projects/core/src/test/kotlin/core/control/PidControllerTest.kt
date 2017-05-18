package core.control

import core.control.PidController.Gains

import org.junit.Assert
import org.junit.Test

class PidControllerTest {
    companion object {
        const val DELTA = 0.0001
    }

    @Test
    fun zeroValuesProducesZeroOutput() {
        val clamp = { x: Double -> x }
        val controller = PidController(Gains(kp = 1.0, ki = 1.0, kd = 1.0), clamp)

        Assert.assertEquals(0.0, controller.nextOutput(), DELTA)
    }

    @Test
    fun startingAtTheSetpointProducesZeroOutput() {
        val clamp = { x: Double -> x }
        val e = { a: Double, b: Double -> a - b }
        val setpoint = 10.0
        val controller = PidController(Gains(kp = 1.0, ki = 1.0, kd = 1.0), clamp)

        controller.addError(e(setpoint, 10.0), 1)

        Assert.assertEquals(0.0, controller.nextOutput(), DELTA)
    }

    @Test
    fun startingHalfwayProducesHalfOutput() {
        val clamp = { x: Double -> x }
        val e = { a: Double, b: Double -> a - b }
        val setpoint = 10.0
        val controller = PidController(Gains(kp = 1.0, ki = 1.0, kd = 1.0), clamp)

        controller.addError(e(setpoint, 5.0), 1)

        Assert.assertEquals(5.0, controller.nextOutput(), DELTA)
    }
}
