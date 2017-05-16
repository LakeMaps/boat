package core

import microcontrollers.PropulsionMicrocontroller

class PropulsionSystem(private val propulsionMicrocontroller: PropulsionMicrocontroller) {
    fun setSpeed(l: Double, r: Double) {
        val m0 = rangeMap(l, -1.0..1.0, PropulsionMicrocontroller.OUTPUT_RANGE).toShort()
        val m1 = rangeMap(r, -1.0..1.0, PropulsionMicrocontroller.OUTPUT_RANGE).toShort()
        propulsionMicrocontroller.setSpeed(m0, m1)
    }

    private fun rangeMap(value: Double, from: ClosedRange<Double>, to: ClosedRange<Double>): Double {
        require(value in from, { "value $value must be in source range $from" })
        return to.start + (to.endInclusive - to.start) * (value - from.start) / (from.endInclusive - from.start)
    }
}
