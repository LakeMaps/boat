package units

import units.UnitConverter.convert

data class Quantity<T : Unit<T>, U : Unit<T>>(val value: Long, private val unit: U) {
    constructor(value: Int, unit: U): this(value.toLong(), unit)

    constructor(value: Double, unit: U): this(value.toLong(), unit)

    operator fun <O: Unit<T>> plus(o: Quantity<T, O>) = Quantity(value + convert(o.value, o.unit, unit), unit)

    override fun toString() = "$value$unit"
}
