package com.github.ericytsang.lib.observe

import java.util.LinkedHashSet

/**
 * Created by surpl on 6/21/2016.
 */
class SimpleObservableSet<V>(val wrapee:MutableSet<V>):MutableSet<V>,KeylessChange.Observable<V>
{
    override val observers = LinkedHashSet<KeylessChange.Observer<V>>()
    override val collection = object:Collection<V>
    {

    }

    override val size:Int get() = wrapee.size
    override fun isEmpty():Boolean = wrapee.isEmpty()
    override fun contains(element:V):Boolean = wrapee.contains(element)
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

    override fun addAll(elements:Collection<V>):Boolean = elements.map {add(it)}.any()
    override fun add(element:V):Boolean
    {
        return if (wrapee.add(element))
        {
            val change = KeylessChange(
                observable = this,
                added = setOf(element))
            observers.forEach {it.onChange(change)}
            true
        }
        else
        {
            false
        }
    }

    override fun clear()
    {
        val change = KeylessChange(
            observable = this,
            removed = wrapee.toSet())
        wrapee.clear()
        observers.forEach {it.onChange(change)}
    }

    override fun removeAll(elements:Collection<V>):Boolean = elements.map {remove(it)}.any()
    override fun retainAll(elements:Collection<V>):Boolean = wrapee.filter {it !in elements}.map {remove(it)}.any()
    override fun remove(element:V):Boolean
    {
        return if (wrapee.remove(element))
        {
            val change = KeylessChange(
                observable = this,
                removed = setOf(element))
            observers.forEach {it.onChange(change)}
            true
        }
        else
        {
            false
        }
    }
}
