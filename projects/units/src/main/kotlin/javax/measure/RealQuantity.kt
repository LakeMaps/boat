package javax.measure

data class RealQuantity<Q: Quantity<Q>>(override val value: Double, override val unit: Unit<Q>) : Quantity<Q> {
    override fun add(augend: Quantity<Q>) = RealQuantity(value + augend.value.toDouble(), unit)

    override fun subtract(subtrahend: Quantity<Q>): Quantity<Q> = RealQuantity(value - subtrahend.value.toDouble(), unit)

    override fun multiply(multiplier: Number) = RealQuantity(value * multiplier.toDouble(), unit)

    override fun divide(divisor: Number) = RealQuantity(value / divisor.toDouble(), unit)

    override fun toString() = "$value$unit"
}
