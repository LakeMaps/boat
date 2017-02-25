package gps

/**
 * A set of dilution of precision (DOP) values.
 *
 * @property position Position dilution of precision
 * @property horizontal Horizontal dilution of precision
 * @property vertical Vertical dilution of precision
 */
data class GpsDilutionOfPrecision(val position: Double? = null, val horizontal: Double? = null, val vertical: Double? = null)
