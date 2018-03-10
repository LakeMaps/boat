package gps.parser

import org.junit.Assert
import org.junit.Test

class SentenceTest {
    @Test
    fun sentenceChecksumIsCorrect() {
        val s = Sentence("GP", "VTG", arrayOf("165.48", "T", "", "M", "0.03", "N", "0.06", "K", "A"))
        Assert.assertTrue(s.checksum == 54)
    }

    @Test
    fun sentenceStringRepresentationIsCorrect() {
        val s = Sentence("GP", "VTG", arrayOf("165.48", "T", "", "M", "0.03", "N", "0.06", "K", "A"))
        Assert.assertEquals(s.toString(), "\$GPVTG,165.48,T,,M,0.03,N,0.06,K,A*36")
    }
}
