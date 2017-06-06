package core.values

import schemas.TypedMessageProtobuf

data class Motion(val surge: Double, val yaw: Double) {
    companion object {
        fun clamp(value: Double) = when {
            value >  1.0 ->  1.0
            value < -1.0 -> -1.0
            else -> value
        }

        fun decode(bytes: ByteArray): Motion {
            val obj = TypedMessageProtobuf.TypedMessage.parseFrom(bytes)
            val motion = obj.motion
            return Motion(motion.surge, motion.yaw)
        }
    }
}
