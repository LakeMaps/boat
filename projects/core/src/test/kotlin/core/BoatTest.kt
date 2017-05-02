package core

import core.broadcast.Broadcast
import core.hardware.Propeller
import core.values.Motion

import java.util.concurrent.TimeUnit

import org.junit.Assert
import org.junit.Test
import rx.broadcast.InMemoryBroadcast
import rx.schedulers.TestScheduler

class BoatTest {
    @Test
    fun testSurgeAloneDoesProduceCorrectPropSpeeds() {
        class NullPropeller : Propeller {
            private var s = 0.0
            override var speed
                get() = s
                set(value) { s = value }
        }

        val broadcast = InMemoryBroadcast()
        val scheduler = TestScheduler()
        val props = Pair(NullPropeller(), NullPropeller())
        val boat = Boat(Broadcast(broadcast), props)

        boat.start(io = scheduler, clock = scheduler)

        broadcast.send(Motion(surge = 1.0, yaw = 0.0)).subscribe()
        scheduler.advanceTimeBy(boat.SLEEP_DURATION_MS, TimeUnit.MILLISECONDS)

        val (l, r) = props
        Assert.assertEquals(l.speed, 1.0, 0.0)
        Assert.assertEquals(r.speed, 1.0, 0.0)
    }

    @Test
    fun testSurgeAndYawDoProduceCorrectPropSpeeds() {
        class NullPropeller : Propeller {
            private var s = 0.0
            override var speed
                get() = s
                set(value) { s = value }
        }

        val broadcast = InMemoryBroadcast()
        val scheduler = TestScheduler()
        val props = Pair(NullPropeller(), NullPropeller())
        val boat = Boat(Broadcast(broadcast), props)

        boat.start(io = scheduler, clock = scheduler)

        broadcast.send(Motion(surge = 0.8, yaw = -0.25)).subscribe()
        scheduler.advanceTimeBy(boat.SLEEP_DURATION_MS, TimeUnit.MILLISECONDS)

        val (l, r) = props
        Assert.assertEquals(l.speed, 0.8000, 0.0001)
        Assert.assertEquals(r.speed, 0.4190, 0.0001)
    }

    @Test
    fun testNoSurgeOrYawDoesProduceCorrectPropSpeeds() {
        class NullPropeller : Propeller {
            private var s = 0.0
            override var speed
                get() = s
                set(value) { s = value }
        }

        val broadcast = InMemoryBroadcast()
        val scheduler = TestScheduler()
        val props = Pair(NullPropeller(), NullPropeller())
        val boat = Boat(Broadcast(broadcast), props)

        boat.start(io = scheduler, clock = scheduler)

        broadcast.send(Motion(surge = 0.0, yaw = 0.0)).subscribe()
        scheduler.advanceTimeBy(boat.SLEEP_DURATION_MS, TimeUnit.MILLISECONDS)

        val (l, r) = props
        Assert.assertEquals(l.speed, 0.0, 0.0001)
        Assert.assertEquals(r.speed, 0.0, 0.0001)
    }
}
