package core.geospatial

import units.Angle
import units.Degree
import units.Degree.convert
import units.Quantity
import units.Radian
import units.Radian.convert

/**
 * Returns the initial heading from [a] to [b]
 *
 * @see [Great-circle distance between 2 points](http://www.movable-type.co.uk/scripts/gis-faq-5.1.html)
 * @see [Calculate distance between two points](http://www.movable-type.co.uk/scripts/latlong.html)
 * @return the initial heading from [a] to [b]
 */
fun bearing(a: Point, b: Point): Quantity<Angle, Degree> {
    val (longitude1, latitude1) = a
    val (longitude2, latitude2) = b

    val (λ1, φ1) = Pair(longitude1.convert<Radian>(Radian).value, latitude1.convert<Radian>(Radian).value)
    val (λ2, φ2) = Pair(longitude2.convert<Radian>(Radian).value, latitude2.convert<Radian>(Radian).value)

    val y = Math.sin(λ2 - λ1) * Math.cos(φ2)
    val x = Math.cos(φ1) * Math.sin(φ2) - Math.sin(φ1) * Math.cos(φ2) * Math.cos(λ2-λ1)

    val decimalDegrees = Quantity(Math.atan2(y, x), Radian).convert<Degree>(Degree).value

    return Quantity((decimalDegrees + 360) % 360, Degree)
}
