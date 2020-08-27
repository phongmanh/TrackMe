package com.manhnguyen.trackme.util

import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

open class EventWrapper<out T>(private val value: T) {
    private val pending = AtomicBoolean(true)

    fun getIfPending(): T? {
        return if (pending.compareAndSet(true, false)) {
            value
        } else {
            null
        }
    }

    fun peek(): T = value

    fun isPending(): Boolean = pending.get()
}

class EventObserver<T>(private val observe: (T) -> Unit) : Observer<EventWrapper<T>> {
    override fun onChanged(event: EventWrapper<T>?) {
        event?.getIfPending()?.let { value ->
            observe(value)
        }
    }
}
