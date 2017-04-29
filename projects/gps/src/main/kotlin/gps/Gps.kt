package gps

import gps.parser.Sentence
import gps.parser.SentenceParser
import units.Angle
import units.Length
import units.Milliknot
import units.Millimetre
import units.Nanodegree
import units.Quantity
import units.Speed

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
        val lat = sentence.decimalDegreesOrNull(1, 2)
        val position = lat?.let { GpsPosition(latitude = it, longitude = sentence.decimalDegreesOrNull(3, 4)!!) }
        val dOP = sentence.doubleOrNull(7)?.let { GpsDilutionOfPrecision(horizontal = it) }
        val altitude = sentence.millimetreOrNull(8)
        return GpsFix(time, position, sentence.char(5), sentence.int(6), dOP, altitude, sentence.millimetreOrNull(10))
    }

    internal fun gsa(sentence: Sentence): GpsActiveSatellites {
        val channels = GpsChannelArray((2..15).mapNotNull { sentence.intOrNull(it) }.toIntArray())
        val dilutionOfPrecision = GpsDilutionOfPrecision(sentence.doubleOrNull(14), sentence.doubleOrNull(15), sentence.doubleOrNull(16))
        return GpsActiveSatellites(sentence.char(0), sentence.char(1), channels, dilutionOfPrecision)
    }

    internal fun gsv(sentence: Sentence): GpsSatellitesInView {
        val channel1Id = sentence.intOrNull( 3)
        val channel1 = channel1Id?.let { GpsSatelliteMessage(it, sentence.nanodegreeOrNull( 4), sentence.nanodegreeOrNull( 5), sentence.intOrNull( 6)) }

        val channel2Id = sentence.intOrNull( 7)
        val channel2 = channel2Id?.let { GpsSatelliteMessage(it, sentence.nanodegreeOrNull( 8), sentence.nanodegreeOrNull( 9), sentence.intOrNull(10)) }

        val channel3Id = sentence.intOrNull(11)
        val channel3 = channel3Id?.let { GpsSatelliteMessage(it, sentence.nanodegreeOrNull(12), sentence.nanodegreeOrNull(13), sentence.intOrNull(14)) }

        val channel4Id = sentence.intOrNull(15)
        val channel4 = channel4Id?.let { GpsSatelliteMessage(it, sentence.nanodegreeOrNull(16), sentence.nanodegreeOrNull(17), sentence.intOrNull(18)) }

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
        return GpsNavInfo(datetime, status, position, sentence.milliknotOrNull(6), sentence.nanodegreeOrNull(7), sentence.char(11))
    }

    internal fun vtg(sentence: Sentence): GpsGroundVelocity {
        return GpsGroundVelocity(sentence.nanodegree(0), sentence.milliknot(4), sentence.char(8))
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

    private fun Sentence.milliknotOrNull(index: Int): Quantity<Speed, Milliknot>? {
        val field = String(this.fields[index]);

        if (field == "") {
            return null
        }

        val (firstBit, secondBit) = field.split('.', limit = 2)
        val num = (firstBit + secondBit.padEnd(3, '0')).toLong()
        return Quantity(num, Milliknot)
    }

    private fun Sentence.milliknot(index: Int): Quantity<Speed, Milliknot> {
        val s = String(this.fields[index])
        val (firstBit, secondBit) = s.split('.', limit = 2)
        val num = (firstBit + secondBit.padEnd(3, '0')).toLong()
        return Quantity(num, Milliknot)
    }

    private fun Sentence.millimetreOrNull(index: Int): Quantity<Length, Millimetre>? {
        val s = String(this.fields[index])
        if (s == "") {
            return null
        }

        return Quantity((s.toDouble() * 10e2).toLong(), Millimetre)
    }

    private fun Sentence.nanodegreeOrNull(index: Int): Quantity<Angle, Nanodegree>? {
        return this.fields.getOrNull(index)?.let {
            val s = String(it)
            val parts = s.split('.', limit = 2)
            val a = parts.getOrNull(0)
            val b = parts.getOrNull(1)
            when {
                a == null || a == "" -> null
                b != null -> Quantity((a + b.padEnd(9, '0')).toLong(), Nanodegree)
                else -> Quantity((a + "0".repeat(9)).toLong(), Nanodegree)
            }
        }
    }

    private fun Sentence.nanodegree(index: Int): Quantity<Angle, Nanodegree> {
        val s = String(this.fields[index])
        val (firstBit, secondBit) = s.split('.', limit = 2)
        val num = (firstBit + secondBit.padEnd(9, '0')).toLong()
        return Quantity(num, Nanodegree)
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
    private fun Sentence.decimalDegreesOrNull(index: Int, signIndex: Int): Quantity<Angle, Nanodegree>? {
        return this.fields.getOrNull(index)?.let {
            val s = String(it)

            if (s == "") {
                return null
            }

            val indexOfDecimal = s.indexOf('.')
            val degrees = (s.slice(0..(indexOfDecimal - 3)) + "0".repeat(9)).toLong()
            val decimalMinutes = s.slice((indexOfDecimal - 2)..s.lastIndex).toDouble()
            val decimalDegrees = (decimalMinutes / 0.6 * 10e6).toLong()

            val direction = this.fields.getOrNull(signIndex)
            val sign = when (direction?.let { String(it) }) {
                "S", "W" -> -1L
                else -> 1L
            }

            Quantity(sign * (degrees + decimalDegrees), Nanodegree)
        }
    }
}
