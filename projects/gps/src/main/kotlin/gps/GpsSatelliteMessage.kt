package gps

data class GpsSatelliteMessage(val id: Int, val elevation: Int?, val azimuth: Int?, val signalRatio: Int?)
