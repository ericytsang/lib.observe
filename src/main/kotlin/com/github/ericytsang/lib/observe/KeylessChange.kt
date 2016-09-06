package com.github.ericytsang.lib.observe

/**
 * object that contains data about a change that occurred to a collection.
 */
data class KeylessChange<Value>(val observable:Observable<Value>,val removed:Set<Value> = emptySet(),val added:Set<Value> = emptySet())
{
    interface Observer<Value>
    {
        companion object
        {
            fun <Value> new(_onChange:(KeylessChange<Value>)->Unit):Observer<Value>
            {
                return object:Observer<Value>
                {
                    override fun onChange(keylessChange:KeylessChange<Value>) = _onChange(keylessChange)
                }
            }
        }

        fun onChange(keylessChange:KeylessChange<Value>):Unit
    }

    interface Observable<Value>
    {
        val observers:MutableSet<KeylessChange.Observer<Value>>
    }
}

fun <Value> KeylessChange.Observable<Value>.addAndUpdate(observer:KeylessChange.Observer<Value>)
{
    observers += observer
    observer.onChange(KeylessChange(this))
}

fun <Value> KeylessChange.Observable<Value>.updateAll()
{
    val change = KeylessChange(this)
    observers.forEach {it.onChange(change)}
}
