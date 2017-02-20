package gps

import gps.parser.Sentence
import gps.parser.SentenceParser
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

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
        val position = lat?.let { GpsPosition(latitude = it, longitude = sentence.double(3)) }
        val dOP = sentence.doubleOrNull(7)?.let { GpsDilutionOfPrecision(horizontal = it) }
        val altitude = sentence.doubleOrNull(8)
        return GpsFix(time, position, sentence.char(5), sentence.int(6), dOP, altitude, sentence.doubleOrNull(10))
    }

    internal fun gsa(sentence: Sentence): GpsActiveSatellites {
        val channels = GpsChannelArray((2..15).mapNotNull { sentence.intOrNull(it) }.toIntArray())
        val dilutionOfPrecision = GpsDilutionOfPrecision(sentence.double(14), sentence.double(15), sentence.double(16))
        return GpsActiveSatellites(sentence.char(0), sentence.char(1), channels, dilutionOfPrecision)
    }

    internal fun gsv(sentence: Sentence): GpsSatellitesInView {
        // These are all conditional for the sake of "symmetry", the first channel should always have an ID
        val channel1Id = sentence.intOrNull( 3)
        val channel1 = channel1Id?.let { GpsSatelliteMessage(it, sentence.intOrNull( 4), sentence.intOrNull( 5), sentence.int( 6)) }

        val channel2Id = sentence.intOrNull( 7)
        val channel2 = channel2Id?.let { GpsSatelliteMessage(it, sentence.intOrNull( 8), sentence.intOrNull( 9), sentence.int(10)) }

        val channel3Id = sentence.intOrNull(11)
        val channel3 = channel3Id?.let { GpsSatelliteMessage(it, sentence.intOrNull(12), sentence.intOrNull(13), sentence.int(14)) }

        val channel4Id = sentence.intOrNull(15)
        val channel4 = channel4Id?.let { GpsSatelliteMessage(it, sentence.intOrNull(16), sentence.intOrNull(17), sentence.int(18)) }

        return GpsSatellitesInView(sentence.int(0), sentence.int(1), sentence.int(2), channel1!!, channel2, channel3, channel4)
    }

    internal fun rmc(sentence: Sentence): GpsNavInfo {
        val date = sentence.date(8)
        val time = sentence.time(0)
        val datetime = OffsetDateTime.of(date, time, ZoneOffset.UTC)
        val status = sentence.char(1) == GpsNavInfo.STATUS_VALID
        val position = GpsPosition(sentence.double(4), sentence.double(2))
        return GpsNavInfo(datetime, status, position, sentence.double(6), sentence.double(7), sentence.char(11))
    }

    internal fun vtg(sentence: Sentence): GpsGroundVelocity {
        return GpsGroundVelocity(sentence.double(0), sentence.double(4), sentence.char(8))
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
}
