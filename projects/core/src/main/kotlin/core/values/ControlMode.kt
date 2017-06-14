package core.values

import schemas.ControlModeProtobuf.ControlMode.Mode

sealed class ControlMode {
    companion object {
        fun decode(bytes: ByteArray) = when (typedMessage(bytes).controlMode.mode!!) {
            Mode.MANUAL -> MANUAL
            Mode.WAYPOINT -> WAYPOINT
        }
    }

    object MANUAL: ControlMode()
    object WAYPOINT: ControlMode()
}
