package units

@kotlin.Suppress("unused")
sealed class Unit<U: Unit<U>>(val symbol: String) {
    override fun toString() = symbol
}

sealed class Angle(symbol: String): Unit<Angle>(symbol)
object Degree: Angle("°")

sealed class Length(symbol: String): Unit<Length>(symbol)
object Metre: Length("m")

sealed class Speed(symbol: String): Unit<Speed>(symbol)
object MetrePerSecond: Speed("m")
object Knot: Speed("kn") {
    inline fun <reified T: Speed> Quantity<Speed, Knot>.convert(to: Speed): Quantity<Speed, T> = when (to) {
        Knot -> Quantity(value, to as T)
        MetrePerSecond -> Quantity(value * 0.514444444, to as T)
    }
}
