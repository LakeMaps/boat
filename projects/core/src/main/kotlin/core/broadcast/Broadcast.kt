package core.broadcast

import rx.Observable
import rx.broadcast.Broadcast

class Broadcast(private val broadcast: Broadcast): Broadcast by broadcast {
    inline fun <reified T : Any> valuesOfType(): Observable<T> = valuesOfType(T::class.java)
}
