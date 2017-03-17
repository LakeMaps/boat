package jmh

import core.hardware.Propeller

class NullPropeller: Propeller {
    private var s = 0.0
    override var speed
        get() = s
        set(value) {
            s = value
        }
}
