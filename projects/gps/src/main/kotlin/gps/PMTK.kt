package gps

class PMTK {
    sealed class BaudRate(val baudRate: Int) {
        object BAUD_RATE_57600 : BaudRate(7600)
    }

    class UpdateRate(val milliseconds: Int) {
        init {
            require(milliseconds in 100..10000, {
                "The possible position fix interval values range between 100 and 10000 milliseconds."
            })
        }
    }
}
