package core.control

import core.control.PIDController.Gains

import org.junit.Assert
import org.junit.Test

class PIDControllerTest {
    companion object {
        const val DELTA = 0.0001
    }

    @Test
    fun zeroValuesProducesZeroOutput() {
        val controller = PIDController<Double>(
            setpoint = 10.0,
            dt = 1,
            gains = Gains(kp = 1.0, ki = 1.0, kd = 1.0),
            e = { a, b -> a - b })

        Assert.assertEquals(0.0, controller.nextOutput(), DELTA)
    }

    @Test
    fun startingAtTheSetpointProducesZeroOutput() {
        val controller = PIDController<Double>(
            setpoint = 10.0,
            dt = 1,
            gains = Gains(kp = 1.0, ki = 1.0, kd = 1.0),
            e = { a, b -> a - b })

        controller.add(10.0)

        Assert.assertEquals(0.0, controller.nextOutput(), DELTA)
    }

    @Test
    fun startingHalfwayProducesHalfOutput() {
        val controller = PIDController<Double>(
            setpoint = 10.0,
            dt = 1,
            gains = Gains(kp = 1.0, ki = 1.0, kd = 1.0),
            e = { a, b -> a - b })

        controller.add(5.0)

        Assert.assertEquals(5.0, controller.nextOutput(), DELTA)
    }
}
