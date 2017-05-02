package core.values

import units.Angle
import units.Degree
import units.MetrePerSecond
import units.Quantity
import units.Speed

data class Velocity(val speed: Quantity<Speed, MetrePerSecond>, val trueBearing: Quantity<Angle, Degree>)
