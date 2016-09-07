package com.github.ericytsang.lib.observe

import java.util.LinkedHashSet

/**
 * Created by surpl on 6/21/2016.
 */
class SimpleObservableSet<V>(val wrapee:MutableSet<V>):AbstractMutableSet<V>(),ObservableSet<V>
{
    override val observers = LinkedHashSet<KeylessChange.Observer<V>>()
    override val collection:Collection<V> get() = wrapee

    override val size:Int get() = wrapee.size
    override fun containsAll(elements:Collection<V>):Boolean = wrapee.containsAll(elements)

    override fun hashCode():Int = wrapee.hashCode()
    override fun equals(other:Any?):Boolean = wrapee.equals(other)
    override fun toString():String = wrapee.toString()

    override fun iterator():MutableIterator<V> = ObservableIterator(wrapee.iterator()).apply()
    {
        observers += KeylessChange.Observer.new()
        {
            iteratorChange ->
            val revisedChange = iteratorChange.copy(observable = this)
            this@SimpleObservableSet.observers.forEach {it.onChange(revisedChange)}
        }
    }

    override fun addAll(elements:Collection<V>):Boolean
    {
        val change = KeylessChange(
            observable = this,
            added = elements.filter {it !in this}.toSet())

        return if (change.added.isNotEmpty())
        {
            wrapee.addAll(change.added)
            observers.forEach {it.onChange(change)}
            true
        }
        else
        {
            false
        }
    }

    override fun removeAll(elements:Collection<V>):Boolean
    {
        val change = KeylessChange(
            observable = this,
            removed = elements.filter {it in this}.toSet())

        return if (change.removed.isNotEmpty())
        {
            wrapee.removeAll(change.removed)
            observers.forEach {it.onChange(change)}
            true
        }
        else
        {
            false
        }
    }
}
