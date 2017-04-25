package gps

import gps.parser.Sentence
import gps.parser.SentenceParser

import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.measure.Quantity
import javax.measure.unit.Degree
import javax.measure.unit.Metre
import javax.measure.unit.MetrePerSecond

/**
 * A GPS device.
 *
 * The use of the input and output functions are controlled by [poll]ing the device. Each call
 * to [poll] reads a number of characters from the input function and parses them into a NMEA 0183
 * sentence, which is passed to [callback]. For example:
 *
 *     val serial = Serial("/dev/ttyS0")
 *     val gps = Gps({ serial.read() }, { msg -> })
 *     gps.poll()
 *
 * **Note: this implementation supports `GGA`, `GSA`, `GSV`, `RMC`, and `VTG` sentences only.**
 *
 * @param recv an input function returning a character from the GPS
 * @param callback an output function accepting a GPS sentence
 */
class Gps(recv: () -> Char, private val callback: (Any) -> Unit) {
    private val parser = SentenceParser(recv)

    fun poll() {
        val sentence = parser.nextMessage()
        val type = String(sentence.type)
        when (type) {
            "GGA" -> callback(gga(sentence))
            "GSA" -> callback(gsa(sentence))
            "GSV" -> callback(gsv(sentence))
            "RMC" -> callback(rmc(sentence))
            "VTG" -> callback(vtg(sentence))
        }
    }

    internal fun gga(sentence: Sentence): GpsFix {
        val time = OffsetTime.of(sentence.time(0), ZoneOffset.UTC)
        val lat = sentence.doubleOrNull(1)
        val position = lat?.let { GpsPosition(latitude = Quantity.of(decimalDegrees(it), Degree), longitude = Quantity.Companion.of(decimalDegrees(sentence.double(3)), Degree)) }
        val dOP = sentence.doubleOrNull(7)?.let { GpsDilutionOfPrecision(horizontal = it) }
        val altitude = sentence.doubleOrNull(8)?.let { Quantity.of(it, Metre) }
        return GpsFix(time, position, sentence.char(5), sentence.int(6), dOP, altitude, sentence.doubleOrNull(10)?.let { Quantity.of(it, Metre) })
    }

    internal fun gsa(sentence: Sentence): GpsActiveSatellites {
        val channels = GpsChannelArray((2..15).mapNotNull { sentence.intOrNull(it) }.toIntArray())
        val dilutionOfPrecision = GpsDilutionOfPrecision(sentence.doubleOrNull(14), sentence.doubleOrNull(15), sentence.doubleOrNull(16))
        return GpsActiveSatellites(sentence.char(0), sentence.char(1), channels, dilutionOfPrecision)
    }

    internal fun gsv(sentence: Sentence): GpsSatellitesInView {
        val channel1Id = sentence.intOrNull( 3)
        val channel1Elevation = sentence.intOrNull(4)?.let { Quantity.of(it, Degree) }
        val channel1Azimuth = sentence.intOrNull(5)?.let { Quantity.of(it, Degree) }
        val channel1 = channel1Id?.let { GpsSatelliteMessage(it, channel1Elevation, channel1Azimuth, sentence.intOrNull( 6)) }

        val channel2Id = sentence.intOrNull( 7)
        val channel2Elevation = sentence.intOrNull(8)?.let { Quantity.of(it, Degree) }
        val channel2Azimuth = sentence.intOrNull(9)?.let { Quantity.of(it, Degree) }
        val channel2 = channel2Id?.let { GpsSatelliteMessage(it, channel2Elevation, channel2Azimuth, sentence.intOrNull(10)) }

        val channel3Id = sentence.intOrNull(11)
        val channel3Elevation = sentence.intOrNull(12)?.let { Quantity.of(it, Degree) }
        val channel3Azimuth = sentence.intOrNull(13)?.let { Quantity.of(it, Degree) }
        val channel3 = channel3Id?.let { GpsSatelliteMessage(it, channel3Elevation, channel3Azimuth, sentence.intOrNull(14)) }

        val channel4Id = sentence.intOrNull(15)
        val channel4Elevation = sentence.intOrNull(16)?.let { Quantity.of(it, Degree) }
        val channel4Azimuth = sentence.intOrNull(17)?.let { Quantity.of(it, Degree) }
        val channel4 = channel4Id?.let { GpsSatelliteMessage(it, channel4Elevation, channel4Azimuth, sentence.intOrNull(18)) }

        return GpsSatellitesInView(sentence.int(0), sentence.int(1), sentence.int(2), channel1, channel2, channel3, channel4)
    }

    internal fun rmc(sentence: Sentence): GpsNavInfo {
        val date = sentence.date(8)
        val time = sentence.time(0)
        val datetime = OffsetDateTime.of(date, time, ZoneOffset.UTC)
        val status = sentence.char(1) == GpsNavInfo.STATUS_VALID
        val speed = sentence.doubleOrNull(6)?.let { MetrePerSecond.fromKnots(it) }
        val trueBearing = sentence.doubleOrNull(7)?.let { Quantity.of(it, Degree) }
        val latitude = sentence.doubleOrNull(2)
        val longitude = sentence.doubleOrNull(4)
        val position = if (latitude != null && longitude != null) {
            GpsPosition(Quantity.of(decimalDegrees(longitude), Degree), Quantity.of(decimalDegrees(latitude), Degree))
        } else {
            null
        }
        return GpsNavInfo(datetime, status, position, speed, trueBearing, sentence.char(11))
    }

    internal fun vtg(sentence: Sentence): GpsGroundVelocity {
        return GpsGroundVelocity(Quantity.of(sentence.double(0), Degree), MetrePerSecond.fromKnots(sentence.double(4)), sentence.char(8))
    }

    private fun Sentence.time(index: Int): LocalTime {
        return LocalTime.parse(String(this.fields[index]), DateTimeFormatter.ofPattern("HHmmss.SSS"))
    }

    private fun Sentence.date(index: Int): LocalDate {
        return LocalDate.parse(String(this.fields[index]), DateTimeFormatter.ofPattern("ddMMyy"))
    }

    private fun Sentence.char(index: Int): Char {
        return this.fields[index][0]
    }

    private fun Sentence.intOrNull(index: Int): Int? {
        return this.fields.getOrNull(index)?.let { String(it).toIntOrNull(radix = 10) }
    }

    private fun Sentence.int(index: Int): Int {
        return String(this.fields[index]).toInt(radix = 10)
    }

    private fun Sentence.doubleOrNull(index: Int): Double? {
        return this.fields.getOrNull(index)?.let { String(it).toDoubleOrNull() }
    }

    private fun Sentence.double(index: Int): Double {
        return String(this.fields[index]).toDouble()
    }

    /**
     * Converts a value from `ddmm.mmmm` or `dddmm.mmmm` to decimal degrees.
     *
     * @param value the value in `ddmm.mmmm` or `dddmm.mmmm` format
     * @return the value in decimal degrees
     * @see <a href="https://en.wikipedia.org/wiki/Decimal_degrees">Wikipedia: Decimal Degrees</a>
     */
    private fun decimalDegrees(value: Double) = ((value / 100.0).toInt() + ((value / 100.0 - (value / 100.0).toInt()) / 0.6))
}
