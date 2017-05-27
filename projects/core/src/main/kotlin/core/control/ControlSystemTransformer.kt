package core.control

import rx.Observable
import rx.Observable.Transformer
import rx.Scheduler
import rx.schedulers.Schedulers
import rx.schedulers.Timestamped

class ControlSystemTransformer<T>(private val setpoint: T, private val controlSystems: Observable<ControlSystem<T>>, private val scheduler: Scheduler = Schedulers.computation()): Transformer<T, Double> {
    private data class ValueControlSystemPair<T>(val value: Timestamped<T>, val controlSystem: ControlSystem<T>)

    override fun call(source: Observable<T>): Observable<Double> =
        source.timestamp(scheduler)
            .withLatestFrom(controlSystems, { a, b -> ValueControlSystemPair(a, b) })
            .scan { (previousValue), current: ValueControlSystemPair<T> ->
                val (value, controlSystem) = current
                controlSystem.addValue(setpoint, value.value, (value.timestampMillis - previousValue.timestampMillis))
                current
            }
            .map { (_, controlSystem) -> controlSystem.nextOutput() }
}
