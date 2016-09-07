package com.github.ericytsang.lib.observe

/**
 * Created by Eric on 9/6/2016.
 */
abstract class AbstractMutabeMap<K,V>:MutableMap<K,V>
{
    final override fun containsKey(key:K):Boolean = keys.contains(key)
    final override fun containsValue(value:V):Boolean = values.contains(value)

    final override fun isEmpty():Boolean = size == 0

    /**
     * puts all entries from [from] into this map. returns a map of all replaced entries.
     */
    protected abstract fun doPut(from:Map<out K,V>):Map<K,V>

    final override fun put(key:K,value:V):V?
    {
        return doPut(mapOf(key to value))[key]
    }

    final override fun putAll(from:Map<out K,V>)
    {
        doPut(from)
    }

    /**
     * removes all entries whose key is in [keys]. returns a map of all removed entries.
     */
    protected abstract fun doRemove(keys:Set<K>):Map<K,V>

    final override fun remove(key:K):V
    {
        return doRemove(setOf(key))[key]!!
    }

    final override fun clear()
    {
        doRemove(keys)
    }
}
