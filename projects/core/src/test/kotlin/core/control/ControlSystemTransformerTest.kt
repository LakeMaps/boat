package core.control

import org.junit.Test
import rx.Observable
import rx.observers.TestSubscriber
import rx.schedulers.TestScheduler
import rx.subjects.TestSubject
import java.util.concurrent.TimeUnit

class ControlSystemTransformerTest {
    companion object {
        const val TIMEOUT_MS = 1000L
    }

    @Test
    fun zeroValuesProducesNoOutput() {
        val clamp = { x: Double -> x }
        val e = { a: Double, b: Double -> a - b }
        val setpoint = 10.0
        val controller = PidController(PidController.Gains(kp = 1.0, ki = 1.0, kd = 1.0), clamp)
        val testScheduler = TestScheduler()
        val testSubject = TestSubject.create<Double>(testScheduler)
        val testSubscriber = TestSubscriber<Double>()

        ControlSystemTransformer(setpoint, Observable.just(ControlSystem(controller, e)), testScheduler)
            .call(testSubject)
            .subscribe(testSubscriber)

        testSubject.onCompleted()
        testScheduler.triggerActions()

        testSubscriber.awaitTerminalEvent(TIMEOUT_MS, TimeUnit.MILLISECONDS)
        testSubscriber.assertNoErrors()
        testSubscriber.assertCompleted()
        testSubscriber.assertReceivedOnNext(listOf())
    }

    @Test
    fun initialValueIsSkippedAndProducesZeroOutput() {
        val clamp = { x: Double -> x }
        val e = { a: Double, b: Double -> a - b }
        val setpoint = 10.0
        val controller = PidController(PidController.Gains(kp = 1.0, ki = 1.0, kd = 1.0), clamp)
        val testScheduler = TestScheduler()
        val testSubject = TestSubject.create<Double>(testScheduler)
        val testSubscriber = TestSubscriber<Double>()

        ControlSystemTransformer(setpoint, Observable.just(ControlSystem(controller, e)), testScheduler)
            .call(testSubject)
            .subscribe(testSubscriber)

        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS)
        testSubject.onNext(1.0)
        testScheduler.triggerActions()

        testSubject.onCompleted()
        testScheduler.triggerActions()

        testSubscriber.awaitTerminalEvent(TIMEOUT_MS, TimeUnit.MILLISECONDS)
        testSubscriber.assertNoErrors()
        testSubscriber.assertCompleted()
        testSubscriber.assertReceivedOnNext(listOf(0.0))
    }

    @Test
    fun startingAtTheSetpointProducesZeroOutput() {
        val clamp = { x: Double -> x }
        val e = { a: Double, b: Double -> a - b }
        val setpoint = 10.0
        val controller = PidController(PidController.Gains(kp = 1.0, ki = 1.0, kd = 1.0), clamp)
        val testScheduler = TestScheduler()
        val testSubject = TestSubject.create<Double>(testScheduler)
        val testSubscriber = TestSubscriber<Double>()

        ControlSystemTransformer(setpoint, Observable.just(ControlSystem(controller, e)), testScheduler)
            .call(testSubject)
            .subscribe(testSubscriber)

        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS)
        testSubject.onNext(10.0)
        testScheduler.triggerActions()

        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS)
        testSubject.onNext(10.0)
        testScheduler.triggerActions()

        testSubject.onCompleted()
        testScheduler.triggerActions()

        testSubscriber.awaitTerminalEvent(TIMEOUT_MS, TimeUnit.MILLISECONDS)
        testSubscriber.assertNoErrors()
        testSubscriber.assertCompleted()
        testSubscriber.assertReceivedOnNext(listOf(0.0, 0.0))
    }

    @Test
    fun startingHalfwayProducesHalfOutput() {
        val clamp = { x: Double -> x }
        val e = { a: Double, b: Double -> a - b }
        val setpoint = 10.0
        val controller = PidController(PidController.Gains(kp = 1.0, ki = 1.0, kd = 1.0), clamp)
        val testScheduler = TestScheduler()
        val testSubject = TestSubject.create<Double>(testScheduler)
        val testSubscriber = TestSubscriber<Double>()

        ControlSystemTransformer(setpoint, Observable.just(ControlSystem(controller, e)), testScheduler)
            .call(testSubject)
            .subscribe(testSubscriber)

        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS)
        testSubject.onNext(5.0)
        testScheduler.triggerActions()

        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS)
        testSubject.onNext(5.0)
        testScheduler.triggerActions()

        testSubject.onCompleted()
        testScheduler.triggerActions()

        testSubscriber.awaitTerminalEvent(TIMEOUT_MS, TimeUnit.MILLISECONDS)
        testSubscriber.assertNoErrors()
        testSubscriber.assertCompleted()
        testSubscriber.assertReceivedOnNext(listOf(0.0, 5.0))
    }
}
