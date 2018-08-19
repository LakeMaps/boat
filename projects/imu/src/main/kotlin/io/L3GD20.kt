package io

import com.pi4j.io.i2c.I2CDevice
import java.nio.ByteBuffer

private const val CTRL_REG1 = 0x20
private const val OUT_X_L = 0x28

class L3GD20(private val i2c: I2CDevice): Gyroscope {
    init {
        i2c.write(CTRL_REG1, 0x00)
        i2c.write(CTRL_REG1, 0x0F)
    }

    override fun rateOfRotation(): XYZ {
        val readSize = 6
        val buffer = ByteArray(readSize)
        i2c.read(OUT_X_L or 0x80, buffer, 0, readSize)

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
}
