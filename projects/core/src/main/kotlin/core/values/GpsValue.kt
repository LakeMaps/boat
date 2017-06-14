package core.values

import gps.GpsFix
import gps.GpsNavInfo
import schemas.GpsProtobuf
import schemas.PositionProtobuf
import schemas.TypedMessageProtobuf
import schemas.VelocityProtobuf

data class GpsValue(val horizontalDilutionOfPrecision: Float, val position: Position, val velocity: Velocity): ProtoSerializable {
    companion object {
        private inline fun <T1, T2, R> let(a: T1?, b: T2?, block: (T1, T2) -> R): R?
            = if (a != null && b != null) block(a, b) else null

        private inline fun <T1, T2, T3, R> let(a: T1?, b: T2?, c: T3?, block: (T1, T2, T3) -> R): R?
            = if (a != null && b != null && c != null) block(a, b, c) else null

        fun from(navInfo: GpsNavInfo, fix: GpsFix): GpsValue? {
            val hdop = fix.dilutionOfPrecision?.horizontal
            val altitude = fix.altitude
            val longitude = navInfo.position?.longitude
            val latitude = navInfo.position?.latitude
            val speed = navInfo.speed
            val course = navInfo.course
            val position = let(longitude, latitude, altitude, ::Position)
            val velocity = let(speed, course, ::Velocity)
            return let(hdop?.toFloat(), position, velocity, ::GpsValue)
        }
    }

    override fun encode(): ByteArray {
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
