package core.geospatial

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class BearingTest(private val a: Point, private val b: Point, private val expected: Double) {
    companion object {
        const val DELTA = 0.0001

        @JvmStatic
        @Parameters(name = "bearing from {0} to {1} should be {2} degrees")
        fun data(): Collection<Array<Any>> = listOf(
            arrayOf(Point(  18.250000, -34.100000), Point( -25.056667,   14.89000), 312.4833),
            arrayOf(Point( 124.783333,  48.506667), Point(-153.545000, -27.463333), 114.5923)
        )
    }

    @Test(timeout = 10000)
    fun testBearing() = Assert.assertEquals(expected, bearing(a, b).value, DELTA)
}

