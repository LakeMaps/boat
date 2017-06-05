package core.values

sealed class ControlMode {
    object MANUAL: ControlMode()
    object WAYPOINT: ControlMode()
}
