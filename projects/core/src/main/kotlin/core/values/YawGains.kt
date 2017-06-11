package core.values

import core.control.PidController.Gains

data class YawGains(private val kp: Double, private val ki: Double, private val kd: Double) {
    val gains = Gains(kp, ki, kd)
}
