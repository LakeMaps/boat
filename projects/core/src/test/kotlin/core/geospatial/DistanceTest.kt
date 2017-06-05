package core.geospatial

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class DistanceTest(private val a: Point, private val b: Point, private val d: Double) {
    companion object {
        const val DELTA = 0.000001

        @JvmStatic
        @Parameters(name = "distance from {0} to {1} should be {2} metres")
        fun data(): Collection<Array<Any>> = listOf(
            arrayOf(Point(  18.250000, -34.100000), Point( -25.056667,   14.89000),  7117151.826367605),
            arrayOf(Point( 124.783333,  48.506667), Point(-153.545000, -27.463333), 11685167.014894713),
            arrayOf(Point(  -4.315000, -47.170000), Point(   2.355000, -47.170000),   504052.997236882),
            arrayOf(Point( -37.418333,  39.273333), Point( -15.458333,  39.273333),  1885640.085465152),
            arrayOf(Point( -47.780000,  45.493333), Point( -44.498333,  45.493333),   255777.597465281),
            arrayOf(Point(-176.705000, -32.956667), Point( 177.475000, -32.956667),   542946.688906394)
        )
    }

    @Test(timeout = 10000)
    fun testDistance() = Assert.assertEquals(d, distance(a, b).value, DELTA)
}
