package javax.measure.unit

import javax.measure.Unit
import javax.measure.quantity.Length

object Metre: Unit<Length> {
    override val symbol: String
        get() = "m"

    override fun toString(): String = symbol
}
