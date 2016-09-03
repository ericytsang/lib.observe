package com.github.ericytsang.lib.observe

import java.util.LinkedHashSet

/**
 * Created by surpl on 8/21/2016.
 */
// todo: add test cases!
class ObservableListIterator<E>(val wrapee:MutableListIterator<E>):MutableListIterator<E>
{
    val observers = LinkedHashSet<KeyedChange.Observer<ObservableListIterator<E>,Int,E>>()

    private var getLastReturned:()->IndexedValue<E> = {throw IllegalStateException("neither next nor previous have been called")}

    override fun hasNext():Boolean = wrapee.hasNext()
    override fun hasPrevious():Boolean = wrapee.hasPrevious()
    override fun nextIndex():Int = wrapee.nextIndex()
    override fun previousIndex():Int = wrapee.previousIndex()

    override fun next():E
    {
        val index = nextIndex()
        val element = wrapee.next()
        getLastReturned = {IndexedValue(index,element)}
        return element
    }

    override fun previous():E
    {
        val index = previousIndex()
        val element = wrapee.previous()
        getLastReturned = {IndexedValue(index,element)}
        return element
    }

    override fun remove()
    {
        wrapee.remove()
        val lastReturned = getLastReturned()
        val change = KeyedChange.newRemove(this,lastReturned.index,lastReturned.value)
        getLastReturned = {throw IllegalStateException("remove or add has been called after the last call to next or previous")}
        observers.forEach {it.onChange(change)}
    }

    override fun set(element:E)
    {
        wrapee.set(element)
        val lastReturned = getLastReturned()
        val change = KeyedChange.newSet(this,lastReturned.index,lastReturned.value,element)
        getLastReturned = {lastReturned.copy(value = element)}
        observers.forEach {it.onChange(change)}
    }

    override fun add(element:E)
    {
        wrapee.add(element)
        getLastReturned = {throw IllegalStateException("remove or add has been called after the last call to next or previous")}
        val change = KeyedChange.newAdd(this,previousIndex(),element)
        observers.forEach {it.onChange(change)}
    }
}
