package core.values

import schemas.TypedMessageProtobuf.TypedMessage.Type

import rx.broadcast.Serializer

class ValueSerializer : Serializer<Any> {
    override fun decode(data: ByteArray): Any = when (typedMessage(data).type!!) {
        Type.BOAT_CONFIG -> BoatConfig.decode(data)
        Type.CONTROL_MODE -> ControlMode.decode(data)
        Type.MOTION -> Motion.decode(data)
        Type.WAYPOINT -> Waypoint.decode(data)
        else -> TODO()
    }

    override fun encode(data: Any) = (data as? ProtoSerializable)?.encode() ?: throw ProtoSerializationException()
}
