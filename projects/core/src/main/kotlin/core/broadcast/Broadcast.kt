package core.broadcast

import rx.Observable
import rx.broadcast.Broadcast

inline fun <reified T : Any> Broadcast.valuesOfType(): Observable<T> = valuesOfType(T::class.java)
