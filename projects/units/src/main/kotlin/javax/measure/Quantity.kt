package javax.measure

interface Quantity<Q: Quantity<Q>> {
    companion object {
        fun <Q: Quantity<Q>> of(value: Double, unit: Unit<Q>): Quantity<Q> = RealQuantity(value, unit)

        fun <Q: Quantity<Q>> of(value: Int, unit: Unit<Q>): Quantity<Q> = of(value.toDouble(), unit)

        fun <Q: Quantity<Q>> of(value: Long, unit: Unit<Q>): Quantity<Q> = of(value.toDouble(), unit)
    }

    /**
     * Returns the value of this `Quantity`.
     * @return the value
     */
    val value: Number

    /**
     * Returns the unit of this `Quantity`.
     * @return the unit
     */
    val unit: Unit<Q>

    /**
     * Returns the sum of this `Quantity` with the one specified.
     * @param augend the `Quantity` to be added
     * @return `this + augend`
     */
    fun add(augend: Quantity<Q>): Quantity<Q>

    /**
     * Returns the difference between this `Quantity` and the one specified.
     * @param subtrahend the `Quantity` to be subtracted
     * @return `this - that`
     */
    fun subtract(subtrahend: Quantity<Q>): Quantity<Q>

    /**
     * Returns the product of this `Quantity` with the `Number` value specified.
     * @param multiplier the `Number` multiplier
     * @return `this * multiplier`
     */
    fun multiply(multiplier: Number): Quantity<Q>

    /**
     * Returns the product of this `Quantity` divided by the `Number` specified.
     * @param divisor the `Number` divisor
     * @return `this / that`
     */
    fun divide(divisor: Number): Quantity<Q>
}

