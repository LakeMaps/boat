package core.values

import schemas.TypedMessageProtobuf

data class BoatConfig(val surgeGains: SurgeGains, val surgeDt: Double, val yawGains: YawGains, val yawDt: Double) {
    companion object {
        fun decode(bytes: ByteArray): BoatConfig {
            val obj = TypedMessageProtobuf.TypedMessage.parseFrom(bytes)
            val boatConfig = obj.boatConfig
            val surgeGains = SurgeGains(boatConfig.surgeControllerGains.kp, boatConfig.surgeControllerGains.ki, boatConfig.surgeControllerGains.kd)
            val yawGains = YawGains(boatConfig.yawControllerGains.kp, boatConfig.yawControllerGains.ki, boatConfig.yawControllerGains.kd)
            return BoatConfig(surgeGains, boatConfig.surgeControllerDt, yawGains, boatConfig.yawControllerDt)
        }
    }
}
