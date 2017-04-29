package gps

import units.Angle
import units.Nanodegree
import units.Quantity

/**
 * A GPS position value.
 *
 * @property longitude the longitude
 * @property latitude the latitude
 */
data class GpsPosition(val longitude: Quantity<Angle, Nanodegree>, val latitude: Quantity<Angle, Nanodegree>)
