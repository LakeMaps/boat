package core.values

import units.Angle
import units.Length
import units.Metre
import units.Degree
import units.Quantity

data class Position(
    val longitude: Quantity<Angle, Degree>,
    val latitude: Quantity<Angle, Degree>,
    val elevation: Quantity<Length, Metre>)
