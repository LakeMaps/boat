package core

fun dd(value: Float): Float {
    return ((value / 100f).toInt() + ((value / 100f - (value / 100f).toInt()) / 0.6f))
}
