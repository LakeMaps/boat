Units API
=========

- Requires [Kotlin 1.1][Kotlin]+
- Based on [JSR 363]

Principles
----------

1. Compile-time safety
2. Explicitness

Unit conversions
----------------

Unit conversions are exposed as [extensions] on the `Quantity` class and are defined inside the Unit they are for.

For example, `Knot`s can be converted into in all other units of speed:

```kotlin
sealed class Speed(symbol: String): Unit<Speed>(symbol)
object MetrePerSecond: Speed("m")
object Knot: Speed("kn") {
    inline fun <reified T: Speed> Quantity<Speed, Knot>.convert(to: Speed): Quantity<Speed, T> = when (to) {
        Knot -> Quantity(value, to as T)
        MetrePerSecond -> Quantity(value * 0.514444444, to as T)
    }
}
```

As new units for speed are added, all [when expression]s used to define conversions to `Speed`s will fail to compile and will need to updated to convert to the newly added unit.

  [Kotlin]:https://kotlinlang.org
  [JSR 363]:https://docs.google.com/document/d/12KhosAFriGCczBs6gwtJJDfg_QlANT92_lhxUWO2gCY
  [extensions]:https://kotlinlang.org/docs/reference/extensions.html
  [when expression]:https://kotlinlang.org/docs/reference/control-flow.html#when-expression
