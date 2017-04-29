package core.values

import units.Angle
import units.Milliknot
import units.Nanodegree
import units.Quantity
import units.Speed

data class Velocity(val speed: Quantity<Speed, Milliknot>, val trueBearing: Quantity<Angle, Nanodegree>)
