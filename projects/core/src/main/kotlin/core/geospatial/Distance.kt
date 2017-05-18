package core.geospatial

import core.values.Position
import units.Degree.convert
import units.Length
import units.Metre
import units.Quantity
import units.Radian

fun distance(a: Position, b: Position): Quantity<Length, Metre> {
    val point1 = Point(longitude = a.longitude, latitude = a.latitude)
    val point2 = Point(longitude = b.longitude, latitude = b.latitude)
    return distance(point1, point2)
}

/**
 * Returns the great-circle distance between the given points
 *
 * @see [Haversine formula](https://en.wikipedia.org/wiki/Haversine_formula)
 * @see [Great-circle distance between 2 points](http://www.movable-type.co.uk/scripts/gis-faq-5.1.html)
 * @see [Calculate distance between two points](http://www.movable-type.co.uk/scripts/latlong.html)
 * @return the great-circle distance between the given points
 */
fun distance(point1: Point, point2: Point): Quantity<Length, Metre> {
    val (longitude1, latitude1) = point1
    val (longitude2, latitude2) = point2

    val (λ1, φ1) = Pair(longitude1.convert<Radian>(Radian).value, latitude1.convert<Radian>(Radian).value)
    val (λ2, φ2) = Pair(longitude2.convert<Radian>(Radian).value, latitude2.convert<Radian>(Radian).value)

    val Δφ = (φ2 - φ1)
    val Δλ = (λ2 - λ1)

    val a = Math.sin(Δφ / 2) * Math.sin(Δφ / 2) + Math.cos(φ1) * Math.cos(φ2) * Math.sin(Δλ / 2) * Math.sin(Δλ / 2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    return Quantity(MEAN_RADIUS_EARTH * c, Metre)
}
