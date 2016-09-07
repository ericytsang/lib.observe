package com.github.ericytsang.lib.observe

abstract class AbstractMutableList<E>:MutableList<E>
{
    final override fun iterator():MutableIterator<E>
    {
        return listIterator()
    }

    final override fun listIterator():MutableListIterator<E>
    {
        return listIterator(0)
    }

    final override fun contains(element:E):Boolean
    {
        return containsAll(listOf(element))
    }

    final override fun isEmpty():Boolean
    {
        return size == 0
    }

    final override fun add(element:E):Boolean
    {
        add(size,element)
        return true
    }

    final override fun add(index:Int,element:E)
    {
        addAll(index,listOf(element))
    }

    final override fun addAll(elements:Collection<E>):Boolean
    {
        return addAll(size,elements)
    }

    final override fun clear()
    {
        removeAll(toList())
    }

    final override fun remove(element:E):Boolean
    {
        return removeAll(listOf(element))
    }

    final override fun retainAll(elements:Collection<E>):Boolean
    {
        return removeAll(toList().filter {it !in elements})
    }
}
