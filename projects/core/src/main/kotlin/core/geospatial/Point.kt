package core.geospatial

import units.Angle
import units.Degree
import units.Quantity

data class Point(val longitude: Quantity<Angle, Degree>, val latitude: Quantity<Angle, Degree>) {
    constructor(longitude: Double, latitude: Double): this(Quantity(longitude, Degree), Quantity(latitude, Degree))
}
