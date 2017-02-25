package gps

/**
 * An NMEA 0183 `GSV` sentence.
 *
 * Depending on the number of satellites tracked, multiple `GSV` messages may
 * be required to represent each satellite. The message count and index will
 * note how many more messages exist in addition to this message in order to
 * get a complete list of all of the satellites.
 *
 * @property messageCount the total message count for a round
 * @property messageIndex the message index (out of the message count)
 * @property satellitesInView the number of satellites in view
 * @property channel1 the satellite message on channel 1
 * @property channel2 the satellite message on channel 2
 * @property channel3 the satellite message on channel 3
 * @property channel4 the satellite message on channel 4
 */
data class GpsSatellitesInView(
    val messageCount: Int,
    val messageIndex: Int,
    val satellitesInView: Int,
    val channel1: GpsSatelliteMessage? = null,
    val channel2: GpsSatelliteMessage? = null,
    val channel3: GpsSatelliteMessage? = null,
    val channel4: GpsSatelliteMessage? = null
)
