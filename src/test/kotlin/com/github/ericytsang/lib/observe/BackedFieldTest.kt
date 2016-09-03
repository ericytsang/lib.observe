package com.github.ericytsang.lib.observe

import org.junit.Test

/**
 * Created by surpl on 8/17/2016.
 */
class BackedFieldTest
{
    val changes = mutableListOf<Change<Int>>()

    val noLargerThan7 = object:BackedField<Int>(4)
    {
        override fun FieldAccess<Int>.setter(proposedValue:Int)
        {
            println("im being updated: $proposedValue")
            if (proposedValue > 7)
            {
                field = 7
            }
            else
            {
                field = proposedValue
            }
        }
    }

    init
    {
        noLargerThan7.observers += Change.Observer.new()
        {
            changes.add(it)
        }
    }

    @Test
    fun setLargerThan7()
    {
        noLargerThan7.value = 9
        assert(noLargerThan7.value == 7,{"${noLargerThan7.value}"})
        assert(changes == listOf(
            Change(noLargerThan7,4,7)
        ))
    }

    @Test
    fun setEqualTo7()
    {
        noLargerThan7.value = 7
        assert(noLargerThan7.value == 7,{"${noLargerThan7.value}"})
        assert(changes == listOf(
            Change(noLargerThan7,4,7)
        ))
    }

    @Test
    fun setLessThan7()
    {
        noLargerThan7.value = 2
        assert(noLargerThan7.value == 2,{"${noLargerThan7.value}"})
        assert(changes == listOf(
            Change(noLargerThan7,4,2)
        ))
    }

    @Test
    fun updateAll()
    {
        var count = 0
        noLargerThan7.observers += Change.Observer.new()
        {
            count++
        }
        noLargerThan7.observers += Change.Observer.new()
        {
            count++
        }
        noLargerThan7.updateAll()
        assert(count == 2)
    }

    @Test
    fun addAndUpdate()
    {
        var count = 0
        noLargerThan7.addAndUpdate(Change.Observer.new()
        {
            count++
        })
        noLargerThan7.observers += Change.Observer.new()
        {
            count++
        }
        noLargerThan7.updateAll()
        assert(count == 3)
    }
}