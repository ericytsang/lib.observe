package com.github.ericytsang.lib.observe

/**
 * object that contains data about a change that occurred to a collection.
 */
data class KeyedChange<Key,Value>(val observable:KeyedChange.Observable<Key,Value>,val removed:Map<Key,Value> = emptyMap(),val added:Map<Key,Value> = emptyMap())
{
    interface Observer<Key,Value>
    {
        companion object
        {
            fun <Key,Value> new(_onChange:(KeyedChange<Key,Value>)->Unit):Observer<Key,Value>
            {
                return object:Observer<Key,Value>
                {
                    override fun onChange(keyedChange:KeyedChange<Key,Value>) = _onChange(keyedChange)
                }
            }
        }

        fun onChange(keyedChange:KeyedChange<Key,Value>):Unit
    }

    interface Observable<Key,Value>
    {
        val map:Map<Key,Value>
        val observers:MutableSet<Observer<Key,Value>>
    }
}

fun <Key,Value> KeyedChange.Observable<Key,Value>.addAndUpdate(observer:KeyedChange.Observer<Key,Value>)
{
    observers += observer
    observer.onChange(KeyedChange(this))
}

fun <Key,Value> KeyedChange.Observable<Key,Value>.updateAll()
{
    val change = KeyedChange(this)
    observers.forEach {it.onChange(change)}
}
