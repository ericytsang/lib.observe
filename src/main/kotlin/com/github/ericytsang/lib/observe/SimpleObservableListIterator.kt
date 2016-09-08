package com.github.ericytsang.lib.observe

import com.github.ericytsang.lib.iteratortolistadapter.IteratorToListAdapter
import java.util.AbstractMap
import java.util.LinkedHashSet

/**
 * Created by surpl on 8/21/2016.
 */
// todo: add test cases!
class SimpleObservableListIterator<E>(val wrapee:MutableListIterator<E>):ObservableListIterator<E>
{
    override val map:Map<Int,E> get() = object:Map<Int,E>
    {
        private val list = IteratorToListAdapter(this@SimpleObservableListIterator)
        override val entries:Set<Map.Entry<Int,E>> get() = list
            .mapIndexed {i,e -> AbstractMap.SimpleImmutableEntry(i,e)}
            .toSet()
        override val keys:Set<Int> get() = list.indices.toSet()
        override val size:Int get() = list.size
        override val values:Collection<E> = list
        override fun containsKey(key:Int):Boolean = key in keys
        override fun containsValue(value:E):Boolean = value in values
        override fun get(key:Int):E = list[key]
        override fun isEmpty():Boolean = size == 0
    }
    override val observers = LinkedHashSet<KeyedChange.Observer<Int,E>>()

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
        val change = KeyedChange(
            observable = this,
            removed = mapOf(lastReturned.index to lastReturned.value))
        getLastReturned = {throw IllegalStateException("remove or add has been called after the last call to next or previous")}
        observers.forEach {it.onChange(change)}
    }

    override fun set(element:E)
    {
        wrapee.set(element)
        val lastReturned = getLastReturned()
        val change = KeyedChange(
            observable = this,
            removed = mapOf(previousIndex() to lastReturned.value),
            added = mapOf(previousIndex() to element))
        getLastReturned = {lastReturned.copy(value = element)}
        observers.forEach {it.onChange(change)}
    }

    override fun add(element:E)
    {
        wrapee.add(element)
        getLastReturned = {throw IllegalStateException("remove or add has been called after the last call to next or previous")}
        val change = KeyedChange(
            observable = this,
            added = mapOf(previousIndex() to element))
        observers.forEach {it.onChange(change)}
    }
}
