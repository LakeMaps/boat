package core.values

import core.dd
import gps.GpsFix
import gps.GpsNavInfo
import schemas.GpsProtobuf
import schemas.PositionProtobuf
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
                val position = Position(dd(longitude.toFloat()), dd(latitude.toFloat()), altitude.toFloat())
                GpsValue(hdop.toFloat(), position, Velocity(speed.toFloat(), course.toFloat()))
            } else {
                null
            }
        }
    }

    fun encode(): ByteArray {
        return GpsProtobuf.Gps.newBuilder()
            .setHorizontalDilutionOfPrecision(horizontalDilutionOfPrecision)
            .setPosition(PositionProtobuf.Position.newBuilder()
                .setElevation(position.elevation.toDouble())
                .setLongitude(position.longitude.toDouble())
                .setLatitude(position.latitude.toDouble()))
            .setVelocity(VelocityProtobuf.Velocity.newBuilder()
                .setSpeed(velocity.speed.toDouble())
                .setTrueBearing(velocity.trueBearing.toDouble()))
            .build()
            .toByteArray()
    }
}
