package javax.measure

interface Unit<Q: Quantity<Q>> {
    /**
     * Returns the symbol of this unit.
     * @return the symbol of this unit
     */
    val symbol: String

    /**
     * Returns a string representation of this unit.
     * @return a string representation of this unit
     */
    override fun toString(): String
}
