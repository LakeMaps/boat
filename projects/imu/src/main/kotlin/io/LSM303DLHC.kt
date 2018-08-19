package io

import com.pi4j.io.i2c.I2CDevice
import java.nio.ByteBuffer

private const val CTRL_REG1_A = 0x20
private const val MR_REG_M = 0x02
private const val OUT_X_L_A = 0x28
private const val OUT_X_L_M = 0x03

class LSM303DLHC(private val accelerometerDevice: I2CDevice, private val magDevice: I2CDevice): Accelerometer, Magnetometer {
    init {
        accelerometerDevice.write(CTRL_REG1_A, 0x27)
        magDevice.write(MR_REG_M, 0x00)
    }

    override fun acceleration(): XYZ {
        val readSize = 6
        val buffer = ByteArray(readSize)
        accelerometerDevice.read(OUT_X_L_A or 0x80, buffer, 0, readSize)

        val bytes = buffer.foldRight(ByteBuffer.allocate(readSize)) { byte, acc ->
            acc.put(byte)
            acc
        }

        bytes.flip()
        return with(bytes.asShortBuffer()) {
            val z = get(0)
            val y = get(1)
            val x = get(2)
            XYZ(x.toDouble(), y.toDouble(), z.toDouble())
        }
    }

    override fun magneticFluxDensity(): XYZ {
        val readSize = 6
        val buffer = ByteArray(readSize)
        magDevice.read(OUT_X_L_M, buffer, 0, readSize)

        val bytes = buffer.fold(ByteBuffer.allocate(readSize)) { acc, byte ->
            acc.put(byte)
            acc
        }

        bytes.flip()
        return with(bytes.asShortBuffer()) {
            val x = get(0)
            val z = get(1)
            val y = get(2)
            XYZ(x.toDouble(), y.toDouble(), z.toDouble())
        }
    }
}
