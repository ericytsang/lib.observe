package com.github.ericytsang.lib.observe

/**
 * object that contains data about a change that occurred to a collection.
 */
data class KeylessChange<out Observable,out Value> private constructor(val observable:Observable,val value:Value,val action:Action)
{
    companion object
    {
        fun <Collection,Value> new(source:Collection,value:Value,action:Action):KeylessChange<Collection,Value> = KeylessChange(source,value,action)
        fun <Collection,Value> newAdd(source:Collection,element:Value):KeylessChange<Collection,Value> = KeylessChange(source,element,Action.ADD)
        fun <Collection,Value> newRemove(source:Collection,element:Value):KeylessChange<Collection,Value> = KeylessChange(source,element,Action.REMOVE)
    }

    enum class Action {ADD,REMOVE}

    interface Observer<in Collection,in Value>
    {
        companion object
        {
            fun <Collection,Value> new(_onChange:(KeylessChange<Collection,Value>)->Unit):Observer<Collection,Value>
            {
                return object:Observer<Collection,Value>
                {
                    override fun onChange(keylessChange:KeylessChange<Collection,Value>) = _onChange(keylessChange)
                }
            }
        }

        fun onChange(keylessChange:KeylessChange<Collection,Value>):Unit
    }

    // todo: add observable interface!
}

// todo: add "addAndUpdate" extension method!
// todo: add "updateAll" extension method!
