package com.github.ericytsang.lib.observe

import java.util.LinkedHashSet

/**
 * Created by surpl on 8/21/2016.
 */
class ObservableList<E>(val wrapee:MutableList<E>):MutableList<E>
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
            val change = KeyedChange.newAdd(this,index..(index+elements.size-1),elements)
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
        val change = KeyedChange.newAdd(this,index..index,listOf(element))
        observers.forEach {it.onChange(change)}
    }

    override fun clear()
    {
        val removed = wrapee.toList()
        wrapee.clear()
        val change = KeyedChange.newRemove(this,removed.indices,removed)
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
        val change = KeyedChange.newRemove(this,index..index,listOf(wrapee.removeAt(index)))
        observers.forEach {it.onChange(change)}
        return change.removed!!.single()
    }

    override fun iterator():MutableIterator<E> = listIterator()
    override fun listIterator():MutableListIterator<E> = listIterator(0)
    override fun listIterator(index:Int):MutableListIterator<E> = ObservableListIterator(wrapee.listIterator(index)).apply()
    {
        observers += KeyedChange.Observer.new()
        {
            change ->
            val revisedChange = KeyedChange.new(this@ObservableList,change.key..change.key,change.removed?.let {listOf(it)},change.added?.let {listOf(it)},change.action)
            this@ObservableList.observers.forEach {it.onChange(revisedChange)}
        }
    }

    override fun set(index:Int,element:E):E
    {
        val removed = get(index)
        wrapee[index] = element
        val change = KeyedChange.newSet(this,index..index,listOf(removed),listOf(element))
        observers.forEach {it.onChange(change)}
        return removed
    }

    override fun subList(fromIndex:Int,toIndex:Int):MutableList<E> = ObservableList(wrapee.subList(fromIndex,toIndex)).apply()
    {
        observers += KeyedChange.Observer.new()
        {
            change ->
            val newKey = (change.key.first+fromIndex)..(change.key.last+fromIndex)
            val newChange = change.copy(observable = this@ObservableList,key = newKey)
            this@ObservableList.observers.forEach {it.onChange(newChange)}
        }
    }

    val observers = LinkedHashSet<KeyedChange.Observer<ObservableList<E>,IntRange,Collection<E>>>()
}
