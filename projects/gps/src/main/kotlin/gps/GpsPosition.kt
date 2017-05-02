package gps

import units.Angle
import units.Degree
import units.Quantity

/**
 * A GPS position value.
 *
 * @property longitude the longitude
 * @property latitude the latitude
 */
data class GpsPosition(val longitude: Quantity<Angle, Degree>, val latitude: Quantity<Angle, Degree>)
