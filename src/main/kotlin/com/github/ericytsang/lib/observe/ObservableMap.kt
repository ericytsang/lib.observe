package com.github.ericytsang.lib.observe

import java.util.LinkedHashSet

/**
 * Created by surpl on 6/21/2016.
 */
class ObservableMap<K,V>(val wrapee:MutableMap<K,V>):MutableMap<K,V>
{
    val observers = LinkedHashSet<KeyedChange.Observer<ObservableMap<K,V>,K,V>>()

    override val entries:MutableSet<MutableMap.MutableEntry<K,V>> get() = wrapee.entries
    override val keys:MutableSet<K> get() = wrapee.keys
    override val values:MutableCollection<V> get() = wrapee.values
    override val size:Int get() = wrapee.size
    override fun containsKey(key:K):Boolean = wrapee.containsKey(key)
    override fun containsValue(value:V):Boolean = wrapee.containsValue(value)
    override fun get(key:K):V? = wrapee[key]
    override fun isEmpty():Boolean = wrapee.isEmpty()

    override fun hashCode():Int = wrapee.hashCode()
    override fun equals(other:Any?):Boolean = wrapee.equals(other)
    override fun toString():String = wrapee.toString()

    override fun putAll(from:Map<out K,V>) = from.forEach {put(it.key,it.value)}
    override fun put(key:K,value:V):V?
    {
        if (contains(key))
        {
            val replaced = wrapee.put(key,value) as V
            val change = KeyedChange.newSet(this,key,replaced,value)
            observers.forEach {it.onChange(change)}
            return replaced
        }
        else
        {
            wrapee.put(key,value)
            val change = KeyedChange.newAdd(this,key,value)
            observers.forEach {it.onChange(change)}
            return null
        }
    }

    override fun clear() = keys.toList().forEach {remove(it)}
    override fun remove(key:K):V?
    {
        return if (contains(key))
        {
            val removed = wrapee.remove(key)
            val change = KeyedChange.newRemove(this,key,removed as V)
            observers.forEach {it.onChange(change)}
            removed
        }
        else
        {
            null
        }
    }
}
