package com.github.ericytsang.lib.observe

/**
 * Created by surpl on 9/7/2016.
 */
abstract class AbstractMutableSet<E>:MutableSet<E>
{
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
        return addAll(listOf(element))
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
