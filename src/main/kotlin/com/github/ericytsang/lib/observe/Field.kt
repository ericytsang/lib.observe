package com.github.ericytsang.lib.observe

import java.util.LinkedHashSet

/**
 * Created by surpl on 8/21/2016.
 */
abstract class Field<Value>:Change.Observable<Value>
{
    override var value:Value

        get()
        {
            return getter()
        }

        set(value:Value)
        {
            // set the backing value to something else
            val oldValue = getter()
            setter(value)
            val newValue = getter()

            // notify observers if the value changed
            if (oldValue != newValue)
            {
                @Suppress("UNCHECKED_CAST")
                val change = Change(this,oldValue,newValue)
                observers.forEach {it.onChange(change)}
            }
        }

    override val observers:MutableSet<Change.Observer<Value>> = LinkedHashSet()

    protected abstract fun getter():Value
    protected abstract fun setter(proposedValue:Value)
}

