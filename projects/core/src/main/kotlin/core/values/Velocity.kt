package core.values

import units.Angle
import units.Knot
import units.Degree
import units.Quantity
import units.Speed

data class Velocity(val speed: Quantity<Speed, Knot>, val trueBearing: Quantity<Angle, Degree>)
