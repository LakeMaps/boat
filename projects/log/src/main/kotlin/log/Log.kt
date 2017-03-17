package log

import java.time.LocalDateTime

class Log {
    enum class Level { NONE, ERROR, WARNING, DEBUG }

    companion object {
        val level = Level.valueOf(System.getenv("LOG") ?: "NONE")

        fun wtf(msg: () -> String) {
            when { level < Level.ERROR -> return }

            System.err.println(formatted(msg))
        }

        fun w(msg: () -> String) {
            when { level < Level.WARNING -> return }

            System.err.println(formatted(msg))
        }

        fun d(msg: () -> String) {
            when { level < Level.DEBUG -> return }

            println(formatted(msg = msg))
        }

        internal inline fun formatted(msg: () -> String) = "$level,${LocalDateTime.now()}\t${msg()}"
    }
}
