package core.values

data class BoatConfig(val surgeGains: SurgeGains, val surgeDt: Double, val yawGains: YawGains, val yawDt: Double) {
    companion object {
        fun decode(bytes: ByteArray) = with(typedMessage(bytes).boatConfig) {
            val surgeGains = with(surgeControllerGains) { SurgeGains(kp, ki, kd) }
            val yawGains = with(yawControllerGains) { YawGains(kp, ki, kd) }
            BoatConfig(surgeGains, surgeControllerDt, yawGains, yawControllerDt)
        }
    }
}
