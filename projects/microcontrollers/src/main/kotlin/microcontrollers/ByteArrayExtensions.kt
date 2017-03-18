package microcontrollers

internal fun ByteArray.hex() = this.joinToString(separator = "") { it.hex() }
