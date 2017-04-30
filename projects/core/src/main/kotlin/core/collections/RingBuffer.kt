package core.collections

class RingBuffer(private val size: Int) {
    private var mark = -1

    private var filled: Boolean = false

    private val elements = DoubleArray(size)

    val array: DoubleArray
        get() = when {
            filled -> DoubleArray(size, { elements[(mark + 1 + it) % size] })
            else -> elements.sliceArray(0..mark)
        }

    fun add(item: Double) {
        mark = (mark + 1) % size
        elements[mark] = item
        filled = filled || mark == size - 1
    }
}
