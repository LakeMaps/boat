package core

import core.hardware.Propeller
import core.values.GpsValue
import core.values.Motion
import gps.GpsFix
import gps.GpsNavInfo

import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

import rx.Observable
import rx.Scheduler
import rx.broadcast.Broadcast
import rx.subjects.PublishSubject
import rx.subjects.Subject

class Boat(private val broadcast: Broadcast, private val props: Pair<Propeller, Propeller>) {
    val SLEEP_DURATION_MS = 30L

    private val dead = AtomicBoolean()

    private val killSwitch: Subject<Void, Void> = PublishSubject.create()

    fun start(io: Scheduler, clock: Scheduler) {
        val speeds = broadcast.valuesOfType(Motion::class.java)
            .startWith(Motion(0.0, 0.0))
            .map { speed(it) }
            .onBackpressureLatest()
        val ticks = Observable.interval(SLEEP_DURATION_MS, TimeUnit.MILLISECONDS, clock)
        val positions = Observable.combineLatest(
            broadcast.valuesOfType(GpsFix::class.java),
            broadcast.valuesOfType(GpsNavInfo::class.java),
            { fix, nav -> Pair(fix, nav)}
        )

        positions.observeOn(io)
            .subscribe { (fix, nav) ->
                GpsValue.from(nav, fix).let { broadcast.send(it).toBlocking().subscribe() }
            }

        ticks.withLatestFrom(speeds, { _, s -> s })
            .observeOn(io)
            .takeUntil(killSwitch)
            .subscribe(this::tick, this::fail, { dead.set(true) })
    }

    fun shutdown() {
        killSwitch.onCompleted()
        while (true) {
            if (dead.get()) {
                break
            }
        }

        tick(speed(Motion(0.0, 0.0)))
    }

    private fun tick(outputs: Pair<Double, Double>) {
        val (l, r) = props
        val (a, b) = outputs
        l.speed = a
        r.speed = b
    }

    private fun speed(motion: Motion): Pair<Double, Double> {
        var r: Double
        var l: Double
        val (surge, yaw) = motion

        if (surge == 0.0 && yaw == 0.0) {
            return Pair(0.0, 0.0)
        }

        r = surge + yaw
        l = surge - yaw

        val maxInputMagnitude = maxOf(Math.abs(surge), Math.abs(yaw))
        val maxThrustMagnitude = maxOf(Math.abs(r), Math.abs(l))
        val scalar = maxInputMagnitude / maxThrustMagnitude

        r *= scalar
        l *= scalar

        return Pair(l, r)
    }

    private fun fail(exception: Throwable): Nothing = throw RuntimeException(exception)
}
