package gps.parser

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class SentenceParserTest(private val message: String) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data() : Collection<Array<String>> {
            return listOf(
                // From the FGPMMOPA6H GPS Standalone Module datasheet
                arrayOf("\$GPGGA,064951.000,2307.1256,N,12016.4438,E,1,8,0.95,39.9,M,17.8,M,,*63"),
                arrayOf("\$GPGSA,A,3,29,21,26,15,18,09,06,10,,,,,2.32,0.95,2.11*00"),
                arrayOf("\$GPGSV,3,1,09,29,36,029,42,21,46,314,43,26,44,020,43,15,21,321,39*7D"),
                arrayOf("\$GPGSV,3,2,09,18,26,314,40,09,57,170,44,06,20,229,37,10,26,084,37*77"),
                arrayOf("\$GPGSV,3,3,09,07,,,26*73"),
                arrayOf("\$GPRMC,064951.000,A,2307.1256,N,12016.4438,E,0.03,165.48,260406,3.05,W,A*2C"),
                arrayOf("\$GPVTG,165.48,T,,M,0.03,N,0.06,K,A*36"),
                // Courtesy of Wikipedia
                arrayOf("\$GPAAM,A,A,0.10,N,WPTNME*32"),
                arrayOf("\$GPGGA,092750.000,5321.6802,N,00630.3372,W,1,8,1.03,61.7,M,55.2,M,,*76"),
                arrayOf("\$GPGSA,A,3,10,07,05,02,29,04,08,13,,,,,1.72,1.03,1.38*0A"),
                arrayOf("\$GPGSV,3,1,11,10,63,137,17,07,61,098,15,05,59,290,20,08,54,157,30*70"),
                arrayOf("\$GPGSV,3,2,11,02,39,223,19,13,28,070,17,26,23,252,,04,14,186,14*79"),
                arrayOf("\$GPGSV,3,3,11,29,09,301,24,16,09,020,,36,,,*76"),
                arrayOf("\$GPRMC,092750.000,A,5321.6802,N,00630.3372,W,0.02,31.66,280511,,,A*43"),
                arrayOf("\$GPGGA,092751.000,5321.6802,N,00630.3371,W,1,8,1.03,61.7,M,55.3,M,,*75"),
                arrayOf("\$GPGSA,A,3,10,07,05,02,29,04,08,13,,,,,1.72,1.03,1.38*0A"),
                arrayOf("\$GPGSV,3,1,11,10,63,137,17,07,61,098,15,05,59,290,20,08,54,157,30*70"),
                arrayOf("\$GPGSV,3,2,11,02,39,223,16,13,28,070,17,26,23,252,,04,14,186,15*77"),
                arrayOf("\$GPGSV,3,3,11,29,09,301,24,16,09,020,,36,,,*76")
            )
        }
    }

    @Test(timeout = 10000)
    fun parseMessage() {
        val chars = message.toCharArray()
        var count = 0
        val parser = SentenceParser({ chars[count++] })
        val sentence = parser.nextMessage()
        Assert.assertEquals(sentence.toString(), message)
    }
}

@RunWith(Parameterized::class)
class SentenceParserEdgeCasesTest(msg: String, private val expected: String, private val message: String) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data() : Collection<Array<String>> {
            return listOf(
                // A few edge cases
                arrayOf(
                    "Sentence ending with 0x0D0A",
                    "\$GPVTG,165.48,T,,M,0.03,N,0.06,K,A*36",
                    "\$GPVTG,165.48,T,,M,0.03,N,0.06,K,A" + String(charArrayOf(0x0D.toChar(), 0x0A.toChar()))
                ),
                arrayOf(
                    "Sentence ending with 0x0D",
                    "\$GPVTG,165.48,T,,M,0.03,N,0.06,K,A*36",
                    "\$GPVTG,165.48,T,,M,0.03,N,0.06,K,A" + String(charArrayOf(0x0D.toChar()))
                ),
                arrayOf(
                    "Sentence ending with 0x0A",
                    "\$GPVTG,165.48,T,,M,0.03,N,0.06,K,A*36",
                    "\$GPVTG,165.48,T,,M,0.03,N,0.06,K,A" + String(charArrayOf(0x0A.toChar()))
                )
            )
        }
    }

    @Test()
    fun parseMessage() {
        val chars = message.toCharArray()
        var count = 0
        val parser = SentenceParser({ chars[count++] })
        val sentence = parser.nextMessage()
        Assert.assertEquals(expected, sentence.toString())
    }
}
