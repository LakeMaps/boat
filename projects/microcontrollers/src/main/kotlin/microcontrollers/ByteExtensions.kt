package microcontrollers

internal fun Byte.hex() = Integer.toHexString(this.toInt()).takeLast(2).padStart(2, '0')
