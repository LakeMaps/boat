package gps

import gps.parser.Sentence
import units.Decibel
import units.Degree
import units.Metre
import units.MetrePerSecond
import units.Quantity

import org.junit.Assert
import org.junit.Test

import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.ZoneOffset

class GpsTest {
    @Test
    fun testVtgSentence() {
        val gps = Gps({ '?' }, { })
        val vtg = gps.vtg(Sentence("GP", "VTG", arrayOf("165.48", "T", "", "M", "0.03", "N", "0.06", "K", "A")))

        Assert.assertEquals(GpsGroundVelocity(Quantity(165.48, Degree), Quantity(0.01543333332, MetrePerSecond), 'A'), vtg)
        Assert.assertTrue("Mode should be Autonomous", vtg.mode == GpsGroundVelocity.MODE_AUTONOMOUS)
    }

    @Test
    fun testRmcSentence() {
        val time = OffsetDateTime.of(2006, 4, 26, 6, 49, 51, 0, ZoneOffset.UTC)
        val position = GpsPosition(latitude = Quantity(23.11876, Degree), longitude = Quantity(120.27406333333333, Degree))
        val expectedGpsNavInfo = GpsNavInfo(time, true, position, Quantity(0.01543333332, MetrePerSecond), Quantity(165.48, Degree), 'A')

        val gps = Gps({ '?' }, { })
        val rmc = gps.rmc(Sentence("GP", "RMC", arrayOf("064951.000", "A", "2307.1256", "N", "12016.4438", "E", "0.03", "165.48", "260406", "3.05", "W", "A")))

        Assert.assertEquals(expectedGpsNavInfo, rmc)
        Assert.assertTrue("Mode should be 'Autonomous'", rmc.mode == GpsNavInfo.MODE_AUTONOMOUS)
        Assert.assertTrue("Status should be 'Valid'", rmc.valid)
    }

    @Test
    fun testGsvSentence1() {
        val channel1 = GpsSatelliteMessage(29, Quantity(36, Degree), Quantity( 29, Degree), Quantity(42, Decibel))
        val channel2 = GpsSatelliteMessage(21, Quantity(46, Degree), Quantity(314, Degree), Quantity(43, Decibel))
        val channel3 = GpsSatelliteMessage(26, Quantity(44, Degree), Quantity( 20, Degree), Quantity(43, Decibel))
        val channel4 = GpsSatelliteMessage(15, Quantity(21, Degree), Quantity(321, Degree), Quantity(39, Decibel))
        val expectedGsv = GpsSatellitesInView(3, 1, 9, channel1, channel2, channel3, channel4)

        val gps = Gps({ '?' }, { })
        val gsv = gps.gsv(Sentence("GP", "GSV", arrayOf("3", "1", "09", "29", "36", "029", "42", "21", "46", "314", "43", "26", "44", "020", "43", "15", "21", "321", "39")))

        Assert.assertEquals(expectedGsv, gsv)
    }

    @Test
    fun testGsvSentence2() {
        val channel1 = GpsSatelliteMessage(18, Quantity(26, Degree), Quantity(314, Degree), Quantity(40, Decibel))
        val channel2 = GpsSatelliteMessage( 9, Quantity(57, Degree), Quantity(170, Degree), Quantity(44, Decibel))
        val channel3 = GpsSatelliteMessage( 6, Quantity(20, Degree), Quantity(229, Degree), Quantity(37, Decibel))
        val channel4 = GpsSatelliteMessage(10, Quantity(26, Degree), Quantity( 84, Degree), Quantity(37, Decibel))
        val expectedGsv = GpsSatellitesInView(3, 2, 9, channel1, channel2, channel3, channel4)

        val gps = Gps({ '?' }, { })
        val gsv = gps.gsv(Sentence("GP", "GSV", arrayOf("3", "2", "09", "18", "26", "314", "40", "09", "57", "170", "44", "06", "20", "229", "37", "10", "26", "084", "37")))

        Assert.assertEquals(expectedGsv, gsv)
    }

    @Test
    fun testGsvSentence3() {
        val channel1 = GpsSatelliteMessage(7, null,  null, Quantity(26, Decibel))
        val expectedGsv = GpsSatellitesInView(3, 3, 9, channel1)

        val gps = Gps({ '?' }, { })
        val gsv = gps.gsv(Sentence("GP", "GSV", arrayOf("3", "3", "09", "07", "", "", "26")))

        Assert.assertEquals(expectedGsv, gsv)
    }

    @Test
    fun testGsaSentence() {
        val expectedGsa = GpsActiveSatellites('A', '3', GpsChannelArray(intArrayOf(29, 21, 26, 15, 18, 9, 6, 10)), GpsDilutionOfPrecision(2.32, 0.95, 2.11))

        val gps = Gps({ '?' }, { })
        val gsa = gps.gsa(Sentence("GP", "GSA", arrayOf("A", "3", "29", "21", "26", "15", "18", "09", "06", "10", "", "", "", "", "2.32", "0.95", "2.11")))

        Assert.assertEquals(expectedGsa, gsa)
        Assert.assertTrue("Mode 1 should be 'Automatic'", gsa.mode1 == GpsActiveSatellites.AUTOMATIC_MODE)
        Assert.assertTrue("Mode 2 should be '3D (≧4 SVs used)'", gsa.mode2 == GpsActiveSatellites.MODE_3D)
    }

    @Test
    fun testGgaSentence() {
        val time = OffsetTime.of(6, 49, 51, 0, ZoneOffset.UTC)
        val position = GpsPosition(latitude = Quantity(23.11876, Degree), longitude = Quantity(120.27406333333333, Degree))
        val expected = GpsFix(time, position, '1', 8, GpsDilutionOfPrecision(horizontal = 0.95), Quantity(39.9, Metre), Quantity(17.8, Metre))

        val gps = Gps({ '?' }, { })
        val gsa = gps.gga(Sentence("GP", "GGA", arrayOf("064951.000", "2307.1256", "N", "12016.4438", "E", "1", "8", "0.95", "39.9", "M", "17.8", "M", "", "")))

        Assert.assertEquals(expected, gsa)
    }

    @Test
    fun testEmptyGgaSentence() {
        val expected = GpsFix(OffsetTime.of(6, 49, 51, 0, ZoneOffset.UTC), null, '0', 1, null, null, null)

        val gps = Gps({ '?' }, { })
        val gsa = gps.gga(Sentence("GP", "GGA", arrayOf("064951.000", "", "", "", "", "0", "01", "", "", "", "", "", "", "")))

        Assert.assertEquals(expected, gsa)
    }

    @Test
    fun testRmcSentenceWithoutLock() {
        val time = OffsetDateTime.of(2006, 4, 26, 6, 49, 51, 0, ZoneOffset.UTC)
        val expectedGpsNavInfo = GpsNavInfo(time, false, null, null, null, 'N')

        val gps = Gps({ '?' }, { })
        val rmc = gps.rmc(Sentence("GP", "RMC", arrayOf("064951.000", "V", "", "", "", "", "", "", "260406", "", "", "N")))

        Assert.assertEquals(expectedGpsNavInfo, rmc)
        Assert.assertFalse("Status should NOT be 'Valid'", rmc.valid)
    }

    @Test
    fun testGsvSentenceWithoutLock() {
        val expectedGsv = GpsSatellitesInView(1, 1, 0, null)

        val gps = Gps({ '?' }, { })
        val gsv = gps.gsv(Sentence("GP", "GSV", arrayOf("1", "1", "00")))

        Assert.assertEquals(expectedGsv, gsv)
    }

    @Test
    fun testGsaSentenceWithoutLock() {
        val expectedGsa = GpsActiveSatellites('A', '1', GpsChannelArray(intArrayOf()), GpsDilutionOfPrecision())

        val gps = Gps({ '?' }, { })
        val gsa = gps.gsa(Sentence("GP", "GSA", arrayOf("A", "1", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "")))

        Assert.assertEquals(expectedGsa, gsa)
        Assert.assertTrue("Mode 1 should be 'Automatic'", gsa.mode1 == GpsActiveSatellites.AUTOMATIC_MODE)
        Assert.assertTrue("Mode 2 should be N/A", gsa.mode2 == GpsActiveSatellites.FIX_NOT_AVAILABLE)
    }

    @Test
    fun testGgaSentenceWithoutLock() {
        val expected = GpsFix(OffsetTime.of(6, 49, 51, 0, ZoneOffset.UTC), null, '0', 0, null, null, Quantity(0, Metre))

        val gps = Gps({ '?' }, { })
        val gsa = gps.gga(Sentence("GP", "GGA", arrayOf("064951.000", "", "", "", "", "0", "00", "", "", "M", "0.0", "M", "", "0000")))

        Assert.assertEquals(expected, gsa)
    }

    @Test
    fun testGsvSentenceMissingSomeSignalNoiseRatio() {
        val channel1 = GpsSatelliteMessage( 8, Quantity(78, Degree), Quantity(253, Degree), null)
        val channel2 = GpsSatelliteMessage(27, Quantity(60, Degree), Quantity( 55, Degree), Quantity(21, Decibel))
        val channel3 = GpsSatelliteMessage( 7, Quantity(56, Degree), Quantity(280, Degree), null)
        val channel4 = GpsSatelliteMessage(16, Quantity(34, Degree), Quantity( 98, Degree), Quantity(23, Decibel))
        val expectedGsv = GpsSatellitesInView(3, 1, 12, channel1, channel2, channel3, channel4)

        val gps = Gps({ '?' }, { })
        val gsv = gps.gsv(Sentence("GP", "GSV", arrayOf("3", "1", "12", "08", "78", "253", "", "27", "60", "055", "21", "07", "56", "280", "", "16", "34", "098", "23")))

        Assert.assertEquals(expectedGsv, gsv)
    }
}
