package com.github.ericytsang.lib.observe

/**
 * Created by surpl on 8/25/2016.
 */
data class FieldChange<Value>(val observable:Observable<Value>,val oldValue:Value,val newValue:Value)
{
    interface Observer<Value>
    {
        companion object
        {
            fun <Value> new(_onChange:(FieldChange<Value>)->Unit):Observer<Value> = object:Observer<Value>
            {
                override fun onChange(fieldChange:FieldChange<Value>) = _onChange(fieldChange)
            }
        }

        fun onChange(fieldChange:FieldChange<Value>)
    }

    interface Observable<Value>
    {
        val value:Value
        val observers:MutableSet<Observer<Value>>
    }
}

fun <Value> FieldChange.Observable<Value>.addAndUpdate(observer:FieldChange.Observer<Value>)
{
    observers += observer
    val change = FieldChange(this,value,value)
    observer.onChange(change)
}

fun <Value> FieldChange.Observable<Value>.updateAll()
{
    val change = FieldChange(this,value,value)
    observers.forEach {it.onChange(change)}
}
