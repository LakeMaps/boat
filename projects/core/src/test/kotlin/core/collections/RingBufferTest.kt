package core.collections

import org.junit.Assert
import org.junit.Test

class RingBufferTest {
    companion object {
        private val DELTA = 0.01
    }

    @Test
    fun addDoesAddElementToBuffer() {
        val buffer = RingBuffer(4)
        buffer.add(5.0)
        Assert.assertArrayEquals(doubleArrayOf(5.0), buffer.array, DELTA)
    }

    @Test
    fun addToFullBufferDoesWrapAround() {
        val buffer = RingBuffer(4)
        buffer.add(1.0)
        buffer.add(2.0)
        buffer.add(3.0)
        buffer.add(4.0)
        buffer.add(5.0)
        Assert.assertArrayEquals(doubleArrayOf(2.0, 3.0, 4.0, 5.0), buffer.array, DELTA)
    }

    @Test
    fun arrayIsEmptyWhenBufferIsEmpty() {
        val buffer = RingBuffer(4)
        Assert.assertArrayEquals(doubleArrayOf(), buffer.array, DELTA)
    }
}
