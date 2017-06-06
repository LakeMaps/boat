package core.values

import schemas.TypedMessageProtobuf
import units.Degree
import units.Metre
import units.Quantity

data class Waypoint(val position: Position) {
    companion object {
        fun decode(bytes: ByteArray): Waypoint {
            val obj = TypedMessageProtobuf.TypedMessage.parseFrom(bytes);
            val waypoint = obj.waypoint
            val zeroMetres = Quantity(0, Metre)
            return Waypoint(Position(Quantity(waypoint.longitude, Degree), Quantity(waypoint.latitude, Degree), zeroMetres))
        }
    }
}
