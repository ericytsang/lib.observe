package com.github.ericytsang.lib.observe

import java.util.LinkedHashSet

/**
 * Created by surpl on 8/21/2016.
 */
class ObservableIterator<E>(val wrapee:MutableIterator<E>):MutableIterator<E>
{
    val observers = LinkedHashSet<Change.Observer<E>>()

    private var getLastReturned:()->E = {throw IllegalStateException("next not yet called")}

    override fun hasNext():Boolean = wrapee.hasNext()

    override fun next():E
    {
        val element = wrapee.next()
        getLastReturned = {element}
        return element
    }

    override fun remove()
    {
        wrapee.remove()
        val lastReturned = getLastReturned()
        val change = Change(this,lastReturned)
        getLastReturned = {throw IllegalStateException("remove already called for last call to next")}
        observers.forEach {it.onChange(change)}
    }

    data class Change<Value>(val observable:ObservableIterator<Value>,val removedValue:Value)
    {
        interface Observer<Value>
        {
            companion object
            {
                fun <Value> new(_onChange:(Change<Value>)->Unit):Observer<Value>
                {
                    return object:Observer<Value>
                    {
                        override fun onChange(change:Change<Value>) = _onChange(change)
                    }
                }
            }

            fun onChange(change:Change<Value>):Unit
        }
    }
}
