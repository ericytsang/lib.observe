package com.github.ericytsang.lib.observe

import com.github.ericytsang.lib.observe.Field

open class BackedField<Value>(initialValue:Value):Field<Value>()
{
    private var field = initialValue

    final override fun getter():Value = with(FieldAccess({field},{field = it}))
    {
        getter()
    }

    final override fun setter(proposedValue:Value) = with(FieldAccess({field},{field = it}))
    {
        setter(proposedValue)
    }

    protected open fun FieldAccess<Value>.getter():Value = field

    protected open fun FieldAccess<Value>.setter(proposedValue:Value)
    {
        field = proposedValue
    }

    class FieldAccess<Value> internal constructor(private val getter:()->Value,private val setter:(Value)->Unit)
    {
        var field:Value
            get() = getter()
            set(value) = setter(value)
    }
}