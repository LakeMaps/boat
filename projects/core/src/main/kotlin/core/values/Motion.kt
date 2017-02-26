package core.values

import schemas.MotionProtobuf

data class Motion(val surge: Double, val yaw: Double) {
    companion object {
        fun decode(bytes: ByteArray): Motion {
            val protobuf = MotionProtobuf.Motion.parseFrom(bytes)
            return Motion(protobuf.surge, protobuf.yaw)
        }
    }

    fun encode(): ByteArray {
        return MotionProtobuf.Motion.newBuilder()
            .setSurge(surge)
            .setYaw(yaw)
            .build()
            .toByteArray()
    }
}
