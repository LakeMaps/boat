@file:JvmName("Main")

package bin

import com.pi4j.io.i2c.I2CFactory
import io.L3GD20
import io.LSM303DLHC
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

fun main(args: Array<String>) {
    val i2CBus = I2CFactory.getInstance(1)
    val accelerometerMagnetometer = LSM303DLHC(i2CBus.getDevice(0b0011001), i2CBus.getDevice(0b0011110))
    val gyroscope = L3GD20(i2CBus.getDevice(0b1101011))

    // If this runs and print out three values we good
    val b = AtomicBoolean(true)
    Runtime.getRuntime().addShutdownHook(Thread({
        b.set(false)
    }))
    while (b.get()) {
        println(gyroscope.rateOfRotation())
//        println(accelerometerMagnetometer.acceleration())
//        println(accelerometerMagnetometer.magneticFluxDensity())
        TimeUnit.SECONDS.sleep(1)
    }
}
