package com.github.ericytsang.lib.observe

/**
 * object that contains data about a change that occurred to a collection.
 */
data class KeyedChange<out Observable,out Key,out Value> private constructor(val observable:Observable,val key:Key,val removed:Value?,val added:Value?,val action:Action)
{
    companion object
    {
        fun <Collection,Key,Value> new(source:Collection,key:Key,removed:Value?,added:Value?,action:Action):KeyedChange<Collection,Key,Value> = KeyedChange(source,key,removed,added,action)
        fun <Collection,Key,Value> newAdd(source:Collection,key:Key,element:Value):KeyedChange<Collection,Key,Value> = KeyedChange(source,key,null,element,Action.ADD)
        fun <Collection,Key,Value> newRemove(source:Collection,key:Key,element:Value):KeyedChange<Collection,Key,Value> = KeyedChange(source,key,element,null,Action.REMOVE)
        fun <Collection,Key,Value> newSet(source:Collection,key:Key,removedElement:Value,replacementElement:Value):KeyedChange<Collection,Key,Value> = KeyedChange(source,key,removedElement,replacementElement,Action.SET)
    }

    enum class Action {ADD,REMOVE,SET}

    interface Observer<in Collection,in Key,in Value>
    {
        companion object
        {
            fun <Collection,Key,Value> new(_onChange:(KeyedChange<Collection,Key,Value>)->Unit):Observer<Collection,Key,Value>
            {
                return object:Observer<Collection,Key,Value>
                {
                    override fun onChange(keyedChange:KeyedChange<Collection,Key,Value>) = _onChange(keyedChange)
                }
            }
        }

        fun onChange(keyedChange:KeyedChange<Collection,Key,Value>):Unit
    }

    // todo: add observable interface!
}

// todo: add "addAndUpdate" extension method!
// todo: add "updateAll" extension method!
