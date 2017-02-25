package gps

/**
 * A satellite message.
 *
 * @property id the satellite ID (from 1 to 32)
 * @property elevation the elevation in degrees
 * @property azimuth the azimuth measurement in degrees
 * @property signalRatio the signal-to-noise ratio in dBHz
 */
data class GpsSatelliteMessage(val id: Int, val elevation: Int?, val azimuth: Int?, val signalRatio: Int?)
