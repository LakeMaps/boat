package units

data class Quantity<T : Unit<T>, U : Unit<T>>(val value: Double, private val unit: U) {
    constructor(value: Int, unit: U): this(value.toDouble(), unit)

    constructor(value: Long, unit: U): this(value.toDouble(), unit)

    operator fun plus(o: Quantity<T, U>) = Quantity(value + o.value, unit)

    operator fun minus(o: Quantity<T, U>) = Quantity(value - o.value, unit)

    override fun toString() = "$value$unit"
}
