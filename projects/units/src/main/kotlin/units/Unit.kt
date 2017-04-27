package units

sealed class Unit<U: Unit<U>>(val symbol: String) {
    override fun toString() = symbol
}

sealed class Angle(symbol: String): Unit<Angle>(symbol)
object Nanodegree: Angle("⋅10e-9°")

sealed class Frequency(symbol: String): Unit<Frequency>(symbol)
object Hertz: Frequency("Hz")

sealed class Length(symbol: String): Unit<Length>(symbol)
object Millimetre: Length("mm")

sealed class Speed(symbol: String): Unit<Speed>(symbol)
object Milliknot: Speed("⋅10e−3kn")
