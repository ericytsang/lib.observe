package com.github.ericytsang.lib.observe

/**
 * Created by surpl on 8/25/2016.
 */
data class Change<Value>(val observable:Observable<Value>,val oldValue:Value,val newValue:Value)
{
    interface Observer<Value>
    {
        companion object
        {
            fun <Value> new(_onChange:(Change<Value>)->Unit):Observer<Value> = object:Observer<Value>
            {
                override fun onChange(change:Change<Value>) = _onChange(change)
            }
        }

        fun onChange(change:Change<Value>)
    }

    interface Observable<Value>
    {
        val value:Value
        val observers:MutableSet<Observer<Value>>
    }
}

fun <Value> Change.Observable<Value>.addAndUpdate(observer:Change.Observer<Value>)
{
    observers += observer
    val change = Change(this,value,value)
    observer.onChange(change)
}

fun <Value> Change.Observable<Value>.updateAll()
{
    val change = Change(this,value,value)
    observers.forEach {it.onChange(change)}
}
