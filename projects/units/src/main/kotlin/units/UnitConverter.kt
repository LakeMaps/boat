package units

object UnitConverter {
    fun <T: Unit<T>> convert(value: Long, fromUnit: Unit<T>, toUnit: Unit<T>) = when (fromUnit) {
        is Angle     -> convertUnits(value, fromUnit, toUnit as Angle    )
        is Frequency -> convertUnits(value, fromUnit, toUnit as Frequency)
        is Length    -> convertUnits(value, fromUnit, toUnit as Length   )
        is Speed     -> convertUnits(value, fromUnit, toUnit as Speed    )
    }

    fun convertUnits(value: Long, from: Angle, to: Angle) = when (from) {
        Nanodegree -> when (to) {
            Nanodegree -> (value)
        }
    }

    fun convertUnits(value: Long, from: Frequency, to: Frequency) = when (from) {
        Hertz -> when (to) {
            Hertz -> value
        }
    }

    fun convertUnits(value: Long, from: Length, to: Length) = when (from) {
        Millimetre -> when (to) {
            Millimetre -> (value)
        }
    }

    fun convertUnits(value: Long, from: Speed, to: Speed) = when (from) {
        Milliknot -> when (to) {
            Milliknot -> value
        }
    }

    fun fromMilli(value: Long) = value * 10e-3
    fun fromNano(value: Long) = value * 10e-9
}
