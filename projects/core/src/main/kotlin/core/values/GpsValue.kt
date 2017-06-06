package core.values

import gps.GpsFix
import gps.GpsNavInfo
import schemas.GpsProtobuf
import schemas.PositionProtobuf
import schemas.TypedMessageProtobuf
import schemas.VelocityProtobuf

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
        return TypedMessageProtobuf.TypedMessage.newBuilder()
            .setType(TypedMessageProtobuf.TypedMessage.Type.GPS)
            .setGps(GpsProtobuf.Gps.newBuilder()
                .setHorizontalDilutionOfPrecision(horizontalDilutionOfPrecision)
                .setPosition(PositionProtobuf.Position.newBuilder()
                    .setElevation(position.elevation.value)
                    .setLongitude(position.longitude.value)
                    .setLatitude(position.latitude.value))
                .setVelocity(VelocityProtobuf.Velocity.newBuilder()
                    .setSpeed(velocity.speed.value)
                    .setTrueBearing(velocity.trueBearing.value)))
            .build()
            .toByteArray()
    }
}
