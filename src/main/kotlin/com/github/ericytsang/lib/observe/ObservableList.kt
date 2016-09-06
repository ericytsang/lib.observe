package com.github.ericytsang.lib.observe

import java.util.LinkedHashSet

/**
 * Created by surpl on 8/21/2016.
 */
class ObservableList<E>(val wrapee:MutableList<E>):MutableList<E>,KeyedChange.Observable<Int,E>
{
    override val size:Int get() = wrapee.size
    override fun contains(element:E):Boolean = wrapee.contains(element)
    override fun containsAll(elements:Collection<E>):Boolean = wrapee.containsAll(elements)
    override fun get(index:Int):E = wrapee[index]
    override fun indexOf(element:E):Int = wrapee.indexOf(element)
    override fun isEmpty():Boolean = wrapee.isEmpty()
    override fun lastIndexOf(element:E):Int = wrapee.lastIndexOf(element)

    override fun equals(other:Any?):Boolean = wrapee.equals(other)
    override fun hashCode():Int = wrapee.hashCode()
    override fun toString():String = wrapee.toString()

    override fun addAll(elements:Collection<E>):Boolean = addAll(size,elements)
    override fun addAll(index:Int,elements:Collection<E>):Boolean
    {
        if (elements.isNotEmpty())
        {
            wrapee.addAll(index,elements)
            val change = KeyedChange(
                observable = this,
                added = elements.mapIndexed { i, e -> index+i to e }.toMap())
            observers.forEach {it.onChange(change)}
            return true
        }
        else
        {
            return false
        }
    }

    override fun add(element:E):Boolean
    {
        add(size,element)
        return true
    }
    override fun add(index:Int,element:E)
    {
        wrapee.add(index,element)
        val change = KeyedChange(
            observable = this,
            added = mapOf(index to element))
        observers.forEach {it.onChange(change)}
    }

    override fun clear()
    {
        val removed = wrapee.toList()
        wrapee.clear()
        val change = KeyedChange(
            observable = this,
            removed = removed.mapIndexed {i,e -> i to e}.toMap())
        observers.forEach {it.onChange(change)}
    }
    override fun remove(element:E):Boolean
    {
        val index = indexOf(element)
        return if (index != -1)
        {
            removeAt(index)
            true
        }
        else
        {
            false
        }
    }
    override fun removeAll(elements:Collection<E>):Boolean = elements.map {remove(it)}.any()
    override fun retainAll(elements:Collection<E>):Boolean = wrapee.filter {it !in elements}.map {remove(it)}.any()
    override fun removeAt(index:Int):E
    {
        val removed = wrapee.removeAt(index)
        val change = KeyedChange(
            observable = this,
            removed = mapOf(index to removed))
        observers.forEach {it.onChange(change)}
        return removed
    }

    override fun iterator():MutableIterator<E> = listIterator()
    override fun listIterator():MutableListIterator<E> = listIterator(0)
    override fun listIterator(index:Int):MutableListIterator<E> = ObservableListIterator(wrapee.listIterator(index)).apply()
    {
        observers += KeyedChange.Observer.new()
        {
            change ->
            val revisedChange = change.copy(observable = this@ObservableList)
            this@ObservableList.observers.forEach {it.onChange(revisedChange)}
        }
    }

    override fun set(index:Int,element:E):E
    {
        val removed = get(index)
        wrapee[index] = element
        val change = KeyedChange(this,mapOf(index to removed),mapOf(index to element))
        observers.forEach {it.onChange(change)}
        return removed
    }

    override fun subList(fromIndex:Int,toIndex:Int):MutableList<E> = ObservableList(wrapee.subList(fromIndex,toIndex)).apply()
    {
        observers += KeyedChange.Observer.new()
        {
            change ->
            val newChange = KeyedChange(
                observable = this@ObservableList,
                removed = change.removed.entries.associate {fromIndex+it.key to it.value},
                added = change.added.entries.associate {fromIndex+it.key to it.value})
            this@ObservableList.observers.forEach {it.onChange(newChange)}
        }
    }

    override val observers = LinkedHashSet<KeyedChange.Observer<Int,E>>()
}
