package core.values

import schemas.TypedMessageProtobuf.TypedMessage

fun typedMessage(bytes: ByteArray): TypedMessage = TypedMessage.parseFrom(bytes)
