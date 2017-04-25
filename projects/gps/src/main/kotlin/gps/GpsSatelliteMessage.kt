package gps

import javax.measure.Quantity
import javax.measure.quantity.Angle

/**
 * A satellite message.
 *
 * @property id the satellite ID (from 1 to 32)
 * @property elevation the elevation
 * @property azimuth the azimuth measurement
 * @property signalRatio the signal-to-noise ratio in dBHz
 */
data class GpsSatelliteMessage(val id: Int, val elevation: Quantity<Angle>?, val azimuth: Quantity<Angle>?, val signalRatio: Int?)
