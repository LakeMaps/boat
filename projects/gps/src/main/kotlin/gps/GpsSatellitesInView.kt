package gps

data class GpsSatellitesInView(
    val messageCount: Int,
    val messageIndex: Int,
    val satellitesInView: Int,
    val channel1: GpsSatelliteMessage? = null,
    val channel2: GpsSatelliteMessage? = null,
    val channel3: GpsSatelliteMessage? = null,
    val channel4: GpsSatelliteMessage? = null
)
