package gps

import gps.parser.Sentence
import gps.parser.SentenceParser
import units.Decibel
import units.Degree
import units.Knot
import units.Knot.convert
import units.Metre
import units.MetrePerSecond
import units.Quantity

import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

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
class Gps(recv: () -> Char, private val send: (ByteArray) -> Unit, private val callback: (Any) -> Unit) {
    private val parser = SentenceParser(recv)

    fun setNmeaBaudRate(baudRate: PMTK.BaudRate) {
        val sentence = Sentence("PMTK", "251", arrayOf(baudRate.baudRate.toString()))
        send((sentence.toString() + "\r\n").toByteArray(Charsets.US_ASCII))
    }

    fun setNmeaUpdateRate(updateRate: PMTK.UpdateRate) {
        val sentence = Sentence("PMTK", "220", arrayOf(updateRate.milliseconds.toString()))
        send((sentence.toString() + "\r\n").toByteArray(Charsets.US_ASCII))
    }

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
        val lat = sentence.decimalDegreesOrNull(1, 2)
        val position = lat?.let { GpsPosition(latitude = it, longitude = sentence.decimalDegreesOrNull(3, 4)!!) }
        val dOP = sentence.doubleOrNull(7)?.let { GpsDilutionOfPrecision(horizontal = it) }
        val altitude = sentence.doubleOrNull(8)?.let { Quantity(it, Metre) }
        val geoidalSeparation = sentence.doubleOrNull(10)?.let { Quantity(it, Metre) }
        return GpsFix(time, position, sentence.char(5), sentence.int(6), dOP, altitude, geoidalSeparation)
    }

    internal fun gsa(sentence: Sentence): GpsActiveSatellites {
        val channels = GpsChannelArray((2..15).mapNotNull { sentence.intOrNull(it) }.toIntArray())
        val dilutionOfPrecision = GpsDilutionOfPrecision(sentence.doubleOrNull(14), sentence.doubleOrNull(15), sentence.doubleOrNull(16))
        return GpsActiveSatellites(sentence.char(0), sentence.char(1), channels, dilutionOfPrecision)
    }

    internal fun gsv(sentence: Sentence): GpsSatellitesInView {
        val channel1Id = sentence.intOrNull(3)
        val channel1Elevation = sentence.doubleOrNull(4)?.let { Quantity(it, Degree) }
        val channel1Azimuth = sentence.doubleOrNull(5)?.let { Quantity(it, Degree) }
        val channel1SignalNoiseRatio = sentence.intOrNull(6)?.let { Quantity(it, Decibel) }
        val channel1 = channel1Id?.let { GpsSatelliteMessage(it, channel1Elevation, channel1Azimuth, channel1SignalNoiseRatio) }

        val channel2Id = sentence.intOrNull(7)
        val channel2Elevation = sentence.doubleOrNull(8)?.let { Quantity(it, Degree) }
        val channel2Azimuth = sentence.doubleOrNull(9)?.let { Quantity(it, Degree) }
        val channel2SignalNoiseRatio = sentence.intOrNull(10)?.let { Quantity(it, Decibel) }
        val channel2 = channel2Id?.let { GpsSatelliteMessage(it, channel2Elevation, channel2Azimuth, channel2SignalNoiseRatio) }

        val channel3Id = sentence.intOrNull(11)
        val channel3Elevation = sentence.doubleOrNull(12)?.let { Quantity(it, Degree) }
        val channel3Azimuth = sentence.doubleOrNull(13)?.let { Quantity(it, Degree) }
        val channel3SignalNoiseRatio = sentence.intOrNull(14)?.let { Quantity(it, Decibel) }
        val channel3 = channel3Id?.let { GpsSatelliteMessage(it, channel3Elevation, channel3Azimuth, channel3SignalNoiseRatio) }

        val channel4Id = sentence.intOrNull(15)
        val channel4Elevation = sentence.doubleOrNull(16)?.let { Quantity(it, Degree) }
        val channel4Azimuth = sentence.doubleOrNull(17)?.let { Quantity(it, Degree) }
        val channel4SignalNoiseRatio = sentence.intOrNull(18)?.let { Quantity(it, Decibel) }
        val channel4 = channel4Id?.let { GpsSatelliteMessage(it, channel4Elevation, channel4Azimuth, channel4SignalNoiseRatio) }

        return GpsSatellitesInView(sentence.int(0), sentence.int(1), sentence.int(2), channel1, channel2, channel3, channel4)
    }

    internal fun rmc(sentence: Sentence): GpsNavInfo {
        val date = sentence.date(8)
        val time = sentence.time(0)
        val datetime = OffsetDateTime.of(date, time, ZoneOffset.UTC)
        val status = sentence.char(1) == GpsNavInfo.STATUS_VALID
        val latitude = sentence.decimalDegreesOrNull(4, 5)
        val longitude = sentence.decimalDegreesOrNull(2, 3)
        val position = if (latitude != null && longitude != null) GpsPosition(latitude, longitude) else null
        val speed = sentence.doubleOrNull(6)?.let { Quantity(it, Knot).convert<MetrePerSecond>(MetrePerSecond) }
        val course = sentence.doubleOrNull(7)?.let { Quantity(it, Degree) }

        return GpsNavInfo(datetime, status, position, speed, course, sentence.char(11))
    }

    internal fun vtg(sentence: Sentence): GpsGroundVelocity {
        return GpsGroundVelocity(Quantity(sentence.double(0), Degree), Quantity(sentence.double(4), Knot).convert(MetrePerSecond), sentence.char(8))
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
     * Returns the sentence field at the given index in decimal degrees.
     *
     * Converts the field value from `ddmm.mmmm` (or `dddmm.mmmm` in the case of longitude) to decimal degrees.
     *
     * @param index the field index
     * @return the value in decimal degrees
     * @see <a href="https://en.wikipedia.org/wiki/Decimal_degrees">Wikipedia: Decimal Degrees</a>
     */
    private fun Sentence.decimalDegreesOrNull(index: Int, signIndex: Int) = this.fields.getOrNull(index)?.let {
        val s = String(it)

        if (s == "") {
            return null
        }

        val indexOfDecimal = s.indexOf('.')
        val degrees = (s.slice(0..(indexOfDecimal - 3))).toDouble()
        val decimalMinutes = s.slice((indexOfDecimal - 2)..s.lastIndex).toDouble()
        val decimalDegrees = (decimalMinutes / 60)

        val direction = this.fields.getOrNull(signIndex)
        val sign = when (direction?.let { String(it) }) {
            "S", "W" -> -1L
            else -> 1L
        }

        Quantity(sign * (degrees + decimalDegrees), Degree)
    }
}
