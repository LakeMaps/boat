package core.values

data class Motion(val surge: Double, val yaw: Double) {
    companion object {
        val ZERO = Motion(0.0, 0.0)

        fun clamp(value: Double) = when {
            value >  1.0 ->  1.0
            value < -1.0 -> -1.0
            else -> value
        }

        fun decode(bytes: ByteArray) = typedMessage(bytes).motion.let { Motion(it.surge, it.yaw) }
    }
}
