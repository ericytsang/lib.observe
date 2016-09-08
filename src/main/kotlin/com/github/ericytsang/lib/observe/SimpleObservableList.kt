package com.github.ericytsang.lib.observe

import java.util.AbstractMap
import java.util.LinkedHashSet

/**
 * Created by surpl on 8/21/2016.
 */
class SimpleObservableList<E>(val wrapee:MutableList<E>):AbstractMutableList<E>(),ObservableList<E>
{
    override val observers = LinkedHashSet<KeyedChange.Observer<Int,E>>()
    override val map:Map<Int,E> get() = object:Map<Int,E>
    {
        override val entries:Set<Map.Entry<Int,E>> get() = wrapee
            .mapIndexed {i,e -> AbstractMap.SimpleImmutableEntry(i,e)}
            .toSet()
        override val keys:Set<Int> get() = indices.toSet()
        override val size:Int get() = wrapee.size
        override val values:Collection<E> get() = wrapee
        override fun containsKey(key:Int):Boolean = key in keys
        override fun containsValue(value:E):Boolean = value in values
        override fun get(key:Int):E = wrapee[key]
        override fun isEmpty():Boolean = size == 0
    }

    override val size:Int get() = wrapee.size
    override fun containsAll(elements:Collection<E>):Boolean = wrapee.containsAll(elements)
    override fun get(index:Int):E = wrapee[index]
    override fun indexOf(element:E):Int = wrapee.indexOf(element)
    override fun lastIndexOf(element:E):Int = wrapee.lastIndexOf(element)

    override fun equals(other:Any?):Boolean = wrapee.equals(other)
    override fun hashCode():Int = wrapee.hashCode()
    override fun toString():String = wrapee.toString()

    override fun addAll(index:Int,elements:Collection<E>):Boolean
    {
        if (elements.isNotEmpty())
        {
            val change = KeyedChange(
                observable = this,
                added = elements.mapIndexed { i, e -> index+i to e }.toMap())
            wrapee.addAll(index,change.added.values)
            observers.forEach {it.onChange(change)}
            return true
        }
        else
        {
            return false
        }
    }

    override fun removeAll(elements:Collection<E>):Boolean
    {
        if (elements.isNotEmpty())
        {
            val removed = this
                .mapIndexedNotNull()
                {
                    i,e ->
                    if (e in elements)
                    {
                        i to e
                    }
                    else
                    {
                        null
                    }
                }
                .toMap()
            val change = KeyedChange(
                observable = this,
                removed = removed)
            wrapee.removeAll(elements)
            observers.forEach {it.onChange(change)}
            return true
        }
        else
        {
            return false
        }
    }

    override fun removeAt(index:Int):E
    {
        val removed = wrapee.removeAt(index)
        val change = KeyedChange(
            observable = this,
            removed = mapOf(index to removed))
        observers.forEach {it.onChange(change)}
        return removed
    }

    override fun listIterator(index:Int):MutableListIterator<E> = SimpleObservableListIterator(wrapee.listIterator(index)).apply()
    {
        observers += KeyedChange.Observer.new()
        {
            change ->
            val revisedChange = change.copy(observable = this@SimpleObservableList)
            this@SimpleObservableList.observers.forEach {it.onChange(revisedChange)}
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

    override fun subList(fromIndex:Int,toIndex:Int):MutableList<E> = SimpleObservableList(wrapee.subList(fromIndex,toIndex)).apply()
    {
        observers += KeyedChange.Observer.new()
        {
            change ->
            val newChange = KeyedChange(
                observable = this@SimpleObservableList,
                removed = change.removed.entries.associate {fromIndex+it.key to it.value},
                added = change.added.entries.associate {fromIndex+it.key to it.value})
            this@SimpleObservableList.observers.forEach {it.onChange(newChange)}
        }
    }
}

