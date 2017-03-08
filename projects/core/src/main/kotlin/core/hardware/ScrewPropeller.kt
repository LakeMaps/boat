package core.hardware

import microcontrollers.PropulsionMicrocontroller

class ScrewPropeller(val controller: PropulsionMicrocontroller, val index: Int) : Propeller {
    private var s: Double = 0.0

    override var speed: Double
        get() = s
        set(value) {
            val output = rangeMap(value, -1.0..1.0, -255.0..255.0).toShort()
            when (index) {
                0 -> controller.setSpeed(output, 0)
                1 -> controller.setSpeed(0, output)
            }
        }

    private fun rangeMap(value: Double, from: ClosedRange<Double>, to: ClosedRange<Double>): Double {
        require(value in from, { "value $value must be in source range $from" })
        return to.start + (to.endInclusive - to.start) * (value - from.start) / (from.endInclusive - from.start)
    }
}
