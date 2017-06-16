package core

import core.values.Motion
import microcontrollers.PropulsionMicrocontroller

class PropulsionSystem(private val propulsionMicrocontroller: PropulsionMicrocontroller) {
    fun setSpeed(motion: Motion) {
        val (l, r) = speed(motion)
        val m0 = rangeMap(l, -1.0..1.0, PropulsionMicrocontroller.OUTPUT_RANGE).toShort()
        val m1 = rangeMap(r, -1.0..1.0, PropulsionMicrocontroller.OUTPUT_RANGE).toShort()
        propulsionMicrocontroller.setSpeed(m0, m1)
    }

    private fun speed(motion: Motion): Pair<Double, Double> {
        var r: Double
        var l: Double
        val (surge, yaw) = motion

        if (surge == 0.0 && yaw == 0.0) {
            return Pair(0.0, 0.0)
        }

        r = surge + yaw
        l = surge - yaw

        val maxInputMagnitude = maxOf(Math.abs(surge), Math.abs(yaw))
        val maxThrustMagnitude = maxOf(Math.abs(r), Math.abs(l))
        val scalar = maxInputMagnitude / maxThrustMagnitude

        r *= scalar
        l *= scalar

        return Pair(l, r)
    }

    private fun rangeMap(value: Double, from: ClosedRange<Double>, to: ClosedRange<Double>): Double {
        require(value in from, { "value $value must be in source range $from" })
        return to.start + (to.endInclusive - to.start) * (value - from.start) / (from.endInclusive - from.start)
    }
}
