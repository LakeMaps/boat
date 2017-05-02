package units

sealed class Unit<U: Unit<U>>(val symbol: String) {
    override fun toString() = symbol
}

sealed class Angle(symbol: String): Unit<Angle>(symbol)
object Degree: Angle("°")

sealed class Length(symbol: String): Unit<Length>(symbol)
object Metre: Length("m")

sealed class Speed(symbol: String): Unit<Speed>(symbol)
object Knot: Speed("kn")
