package javax.measure.unit

import javax.measure.Unit
import javax.measure.quantity.Angle

object Degree: Unit<Angle> {
    override val symbol
        get() = "°"

    override fun toString() = symbol
}
