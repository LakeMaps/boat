package gps

import javax.measure.Quantity
import javax.measure.quantity.Angle

/**
 * A GPS position value.
 *
 * @property longitude the longitude in degrees decimal minute
 * @property latitude the latitude in degrees decimal minute
 */
data class GpsPosition(val longitude: Quantity<Angle>, val latitude: Quantity<Angle>)
