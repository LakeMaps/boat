package jmh

import core.Boat
import core.values.Motion

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Warmup
import rx.broadcast.InMemoryBroadcast
import rx.schedulers.TestScheduler

import java.util.concurrent.TimeUnit

@Fork(1)
@Measurement(iterations = 20)
@Warmup(iterations = 0)
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
open class BoatBenchmark {
    var surge = Math.PI

    var yaw = Math.PI

    @Benchmark
    fun tick(): Pair<NullPropeller, NullPropeller> {
        val broadcast = InMemoryBroadcast()
        val scheduler = TestScheduler()
        val props = Pair(NullPropeller(), NullPropeller())
        val boat = Boat(broadcast, props)

        boat.start(io = scheduler, clock = scheduler)

        broadcast.send(Motion(surge, yaw)).subscribe()
        scheduler.advanceTimeBy(boat.SLEEP_DURATION_MS, TimeUnit.MILLISECONDS)

        return Pair(props.first, props.second)
    }
}
