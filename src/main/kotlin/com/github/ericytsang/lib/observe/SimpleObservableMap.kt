package com.github.ericytsang.lib.observe

import java.util.LinkedHashSet

class SimpleObservableMap<K,V>(val wrapee:MutableMap<K,V>):AbstractMutableMap<K,V>(),ObservableMap<K,V>
{
    override val map:Map<K,V> get() = wrapee
    override val observers = LinkedHashSet<KeyedChange.Observer<K,V>>()

    override val entries:MutableSet<MutableMap.MutableEntry<K,V>> get() = wrapee.entries
    override val keys:MutableSet<K> get() = wrapee.keys
    override val values:MutableCollection<V> get() = wrapee.values
    override fun get(key:K):V? = wrapee[key]

    override fun hashCode():Int = wrapee.hashCode()
    override fun equals(other:Any?):Boolean = wrapee.equals(other)
    override fun toString():String = wrapee.toString()

    override fun doPut(from:Map<out K,V>):Map<K,V>
    {
        val change = KeyedChange(
            observable = this,
            added = from.entries.associate {it.key to it.value},
            removed = wrapee.entries.filter {it.key in from.keys}.associate {it.key to it.value})
        wrapee.putAll(from)
        observers.forEach {it.onChange(change)}
        return change.removed
    }

    override fun doRemove(toRemove:Set<K>):Map<K,V>
    {
        val change = KeyedChange(
            observable = this,
            removed = entries.filter {it.key in toRemove}.associate {it.key to it.value})
        keys.removeAll(toRemove)
        observers.forEach {it.onChange(change)}
        return change.removed
    }
}
