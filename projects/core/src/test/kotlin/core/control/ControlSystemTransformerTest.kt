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

    @Test
    fun testSystem1() {
        val e = { _: Double, value: Double -> value }
        val controller = PidController(PidController.Gains(kp = 1.0, ki = 1.0, kd = 1.0), { x: Double -> x })
        val testScheduler = TestScheduler()
        val testSubject = TestSubject.create<Double>(testScheduler)
        val testSubscriber = TestSubscriber<Double>()

        ControlSystemTransformer(0.0, Observable.just(ControlSystem(controller, e)), testScheduler)
            .call(testSubject)
            .subscribe(testSubscriber)

        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS)
        testSubject.onNext(0.0)
        testScheduler.triggerActions()

        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS)
        testSubject.onNext(5.0)
        testScheduler.triggerActions()

        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS)
        testSubject.onNext(3.0)
        testScheduler.triggerActions()

        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS)
        testSubject.onNext(6.0)
        testScheduler.triggerActions()

        testSubject.onCompleted()
        testScheduler.triggerActions()

        testSubscriber.awaitTerminalEvent(TIMEOUT_MS, TimeUnit.MILLISECONDS)
        testSubscriber.assertNoErrors()
        testSubscriber.assertCompleted()
        testSubscriber.assertReceivedOnNext(listOf(0.0, 5.0, 13.0, 17.0))
    }

    @Test
    fun testSystem2() {
        val e = { _: Double, value: Double -> value }
        val controller = PidController(PidController.Gains(kp = 1.0, ki = 1.0, kd = 1.0), { x: Double -> x })
        val testScheduler = TestScheduler()
        val testSubject = TestSubject.create<Double>(testScheduler)
        val testSubscriber = TestSubscriber<Double>()

        ControlSystemTransformer(0.0, Observable.just(ControlSystem(controller, e)), testScheduler)
            .call(testSubject)
            .subscribe(testSubscriber)

        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS)
        testSubject.onNext(0.0)
        testScheduler.triggerActions()

        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS)
        testSubject.onNext(5.0)
        testScheduler.triggerActions()

        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS)
        testSubject.onNext(6.0)
        testScheduler.triggerActions()

        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS)
        testSubject.onNext(7.0)
        testScheduler.triggerActions()

        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS)
        testSubject.onNext(8.0)
        testScheduler.triggerActions()

        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS)
        testSubject.onNext(9.0)
        testScheduler.triggerActions()

        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS)
        testSubject.onNext(0.0)
        testScheduler.triggerActions()

        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS)
        testSubject.onNext(0.0)
        testScheduler.triggerActions()

        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS)
        testSubject.onNext(1.0)
        testScheduler.triggerActions()

        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS)
        testSubject.onNext(4.0)
        testScheduler.triggerActions()

        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS)
        testSubject.onNext(3.0)
        testScheduler.triggerActions()

        testSubject.onCompleted()
        testScheduler.triggerActions()

        testSubscriber.awaitTerminalEvent(TIMEOUT_MS, TimeUnit.MILLISECONDS)
        testSubscriber.assertNoErrors()
        testSubscriber.assertCompleted()
        testSubscriber.assertReceivedOnNext(listOf(0.0, 5.0, 16.0, 24.0, 33.0, 38.0, 33.0, 17.0, 10.0, 6.0, 12.0))
    }
}
