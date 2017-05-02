package gps

import units.Angle
import units.Degree
import units.Quantity

/**
 * A satellite message.
 *
 * @property id the satellite ID (from 1 to 32)
 * @property elevation the elevation
 * @property azimuth the azimuth
 * @property signalRatio the signal-to-noise ratio in dBHz
 */
data class GpsSatelliteMessage(val id: Int, val elevation: Quantity<Angle, Degree>?, val azimuth: Quantity<Angle, Degree>?, val signalRatio: Int?)
