package core.control

import rx.Observable
import rx.Observable.Transformer
import rx.schedulers.Timestamped

class ControlSystemTransformer<T>(private val setpoint: T, private val controller: ControlSystem<T>): Transformer<T, Double> {
    var last: Timestamped<T>? = null

    override fun call(source: Observable<T>): Observable<Double> =
        source.timestamp()
            .scan(controller, { controller: ControlSystem<T>, curr: Timestamped<T> ->
                val lastTimestampMillis = last?.timestampMillis ?: curr.timestampMillis
                last = curr

                controller.addValue(setpoint, curr.value, (lastTimestampMillis - curr.timestampMillis))
                return@scan controller
            })
            .map(ControlSystem<T>::nextOutput)
}
