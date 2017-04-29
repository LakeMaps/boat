package core.values

import gps.GpsFix
import gps.GpsNavInfo
import schemas.GpsProtobuf
import schemas.PositionProtobuf
import schemas.VelocityProtobuf
import units.UnitConverter

data class GpsValue(val horizontalDilutionOfPrecision: Float, val position: Position, val velocity: Velocity) {
    companion object {
        fun from(foo: GpsNavInfo, bar: GpsFix): GpsValue? {
            val hdop = bar.dilutionOfPrecision?.horizontal
            val altitude = bar.altitude
            val longitude = foo.position?.longitude
            val latitude = foo.position?.latitude
            val speed = foo.speed
            val course = foo.course
            return if (
                   hdop != null
                && altitude != null
                && longitude != null
                && latitude != null
                && speed != null
                && course != null
            ) {
                val position = Position(longitude, latitude, altitude)
                GpsValue(hdop.toFloat(), position, Velocity(speed, course))
            } else {
                null
            }
        }
    }

    fun encode(): ByteArray {
        return GpsProtobuf.Gps.newBuilder()
            .setHorizontalDilutionOfPrecision(horizontalDilutionOfPrecision)
            .setPosition(PositionProtobuf.Position.newBuilder()
                .setElevation(UnitConverter.fromMilli(position.elevation.value))
                .setLongitude(UnitConverter.fromNano(position.longitude.value))
                .setLatitude(UnitConverter.fromNano(position.latitude.value)))
            .setVelocity(VelocityProtobuf.Velocity.newBuilder()
                .setSpeed(UnitConverter.fromMilli(velocity.speed.value))
                .setTrueBearing(UnitConverter.fromNano(velocity.trueBearing.value)))
            .build()
            .toByteArray()
    }
}
