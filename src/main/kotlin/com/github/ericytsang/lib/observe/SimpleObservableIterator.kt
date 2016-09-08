package com.github.ericytsang.lib.observe

import com.github.ericytsang.lib.iteratortolistadapter.IteratorToListAdapter
import java.util.LinkedHashSet

/**
 * Created by surpl on 8/21/2016.
 */
class SimpleObservableIterator<E>(val wrapee:MutableIterator<E>):ObservableIterator<E>
{
    override val collection:Collection<E> get() = IteratorToListAdapter(this)

    override val observers = LinkedHashSet<KeylessChange.Observer<E>>()

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
        val change = KeylessChange(
            observable = this,
            removed = setOf(lastReturned))
        getLastReturned = {throw IllegalStateException("remove already called for last call to next")}
        observers.forEach {it.onChange(change)}
    }
}
