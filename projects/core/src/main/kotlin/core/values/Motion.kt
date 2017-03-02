package core.values

import schemas.MotionProtobuf

data class Motion(val surge: Double, val yaw: Double) {
    companion object {
        const val MESSAGE_SIZE = 18

        fun decode(bytes: ByteArray): Motion {
            if (bytes.size > MESSAGE_SIZE) {
                return decode(bytes.take(MESSAGE_SIZE).toByteArray())
            }

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
