package core.values

import units.Angle
import units.Length
import units.Millimetre
import units.Nanodegree
import units.Quantity

data class Position(val longitude: Quantity<Angle, Nanodegree>, val latitude: Quantity<Angle, Nanodegree>, val elevation: Quantity<Length, Millimetre>)
