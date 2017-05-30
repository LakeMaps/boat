package core.values

import schemas.ControlModeProtobuf
import schemas.TypedMessageProtobuf

sealed class ControlMode {
    companion object {
        fun decode(bytes: ByteArray): ControlMode {
            val obj = TypedMessageProtobuf.TypedMessage.parseFrom(bytes)
            val controlMode = obj.controlMode
            return when (controlMode.mode!!) {
                ControlModeProtobuf.ControlMode.Mode.MANUAL -> MANUAL
                ControlModeProtobuf.ControlMode.Mode.WAYPOINT -> WAYPOINT
            }
        }
    }

    object MANUAL: ControlMode()
    object WAYPOINT: ControlMode()
}
