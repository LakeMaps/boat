package core.control

import core.collections.RingBuffer

class ErrorState(size: Int) {
    private val errors = RingBuffer(size)

    fun add(value: Double) = errors.add(value)

    fun nth(index: Int): Double {
        val errors = errors.array
        if (index < 0) {
            return errors[errors.lastIndex + index]
        }

        return errors[index]
    }

    fun last(): Double {
        val errors =  errors.array
        return errors.last()
    }

    fun sum(f: (Double) -> Double) = errors.array.map(f).sum()
}
