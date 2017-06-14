package core

import core.broadcast.*
import core.control.ControlSystem
import core.control.ControlSystemTransformer
import core.control.Controller
import core.control.PidController
import core.geospatial.bearing
import core.geospatial.distance
import core.values.BoatConfig
import core.values.ControlMode
import core.values.GpsValue
import core.values.Motion
import core.values.Position
import core.values.SurgeGains
import core.values.Waypoint
import core.values.YawGains
import gps.GpsFix
import gps.GpsNavInfo
import units.Degree.convert
import units.Radian

import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

import rx.Observable
import rx.Scheduler
import rx.broadcast.Broadcast
import rx.subjects.PublishSubject
import rx.subjects.Subject

class Boat(private val broadcast: Broadcast, private val propulsionSystem: PropulsionSystem) {
    val SLEEP_DURATION_MS = 100L

    private val dead = AtomicBoolean()

    private val killSwitch: Subject<Void, Void> = PublishSubject.create()

    fun start(io: Scheduler, clock: Scheduler) {
        val ticks = Observable.interval(SLEEP_DURATION_MS, TimeUnit.MILLISECONDS, clock)
        val gpsNavInfo = broadcast.valuesOfType<GpsNavInfo>()
        val gpsFix = broadcast.valuesOfType<GpsFix>()
        val gps = Observable.combineLatest(gpsFix, gpsNavInfo, { fix, nav -> Pair(fix, nav) })
            .flatMap { (fix, nav) -> GpsValue.from(nav, fix)?.let { Observable.just(it) } ?: Observable.empty() }
        val positions = gps.map { it.position }
        val config = broadcast.valuesOfType<BoatConfig>()
            .startWith(BoatConfig(SurgeGains(1.0, 1.0, 1.0), 1.0, YawGains(1.0, 1.0, 1.0), 1.0))
        val surge = broadcast.valuesOfType<Waypoint>()
            .withLatestFrom(config, { w: Waypoint, c: BoatConfig -> Pair(w, c) })
            .switchMap({ (waypoint, config) ->
                val initialController = PidController(config.surgeGains.gains, { Motion.clamp(it) })
                val controlSystems = broadcast.valuesOfType<SurgeGains>()
                    .scan(initialController, { cs, g -> cs.setGains(g.gains) })
                    .map { ControlSystem(it, { a: Position, b: Position -> distance(a, b).value }) }

                positions
                    .onBackpressureDrop()
                    .compose(ControlSystemTransformer(waypoint.position, controlSystems, clock))
            })
        val yaw = broadcast.valuesOfType<Waypoint>()
            .withLatestFrom(config, { w: Waypoint, c: BoatConfig -> Pair(w, c) })
            .switchMap({ (waypoint, config) ->
                val initialController = PidController(config.yawGains.gains, { Motion.clamp(it) })
                val controllers = broadcast.valuesOfType<YawGains>()
                    .scan(initialController, { cs, g -> cs.setGains(g.gains) })
                val controlSystems = gps
                    .withLatestFrom(controllers, { gps: GpsValue, controller: Controller -> Pair(controller, gps) })
                    .map { (c, g) ->
                        ControlSystem(c, fun (a: Position, b: Position): Double {
                            val actualBearing = g.velocity.trueBearing.convert<Radian>(Radian)
                            val desiredBearing = bearing(a, b).convert<Radian>(Radian)
                            val error = (actualBearing - desiredBearing).value
                            return Math.atan2(Math.sin(error), Math.cos(error))
                        })
                    }

                positions
                    .onBackpressureDrop()
                    .compose(ControlSystemTransformer(waypoint.position, controlSystems, clock))
            })
        val speeds = broadcast.valuesOfType<ControlMode>()
            .startWith(ControlMode.MANUAL)
            .switchMap({
                when (it) {
                    ControlMode.MANUAL   -> broadcast.valuesOfType<Motion>()
                    ControlMode.WAYPOINT -> Observable.combineLatest(surge, yaw, ::Motion).startWith(Motion.ZERO)
                }
            })
            .map(this::speed)

        gps.observeOn(io).subscribe { broadcast.send(it).toBlocking().subscribe() }

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

        tick(speed(Motion.ZERO))
    }

    private fun tick(outputs: Pair<Double, Double>) {
        val (a, b) = outputs
        propulsionSystem.setSpeed(a, b)
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
