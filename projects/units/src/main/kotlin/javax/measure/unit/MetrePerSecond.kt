package javax.measure.unit

import javax.measure.Quantity
import javax.measure.Unit
import javax.measure.quantity.Speed

object MetrePerSecond : Unit<Speed> {
    override val symbol: String
        get() = "m/s"

    override fun toString() = symbol

    fun fromKnots(value: Double) = Quantity.of(value * 0.514444, MetrePerSecond)
}
