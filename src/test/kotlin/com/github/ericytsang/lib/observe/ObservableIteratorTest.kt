package com.github.ericytsang.lib.observe

import org.junit.Test

/**
 * Created by surpl on 8/21/2016.
 */
class ObservableIteratorTest
{
    val changes = mutableListOf<ObservableIterator.Change<Int>>()

    val testIterator = ObservableIterator(mutableSetOf(1,2,3,4,5).iterator()).apply()
    {
        observers += ObservableIterator.Change.Observer.new()
        {
            changes.add(it)
        }
    }

    @Test
    fun general()
    {
        assert(testIterator.next() == 1)
        assert(testIterator.next() == 2)
        assert(testIterator.hasNext())
        testIterator.remove()
        assert(testIterator.next() == 3)
        assert(testIterator.hasNext())
        assert(testIterator.next() == 4)
        testIterator.remove()
        assert(testIterator.next() == 5)
        testIterator.remove()
        assert(!testIterator.hasNext())
        assert(changes == listOf(
            ObservableIterator.Change(testIterator,2),
            ObservableIterator.Change(testIterator,4),
            ObservableIterator.Change(testIterator,5)
        ),{"changes: $changes"})
    }

    @Test
    fun consecutiveRemove()
    {
        assert(testIterator.next() == 1)
        assert(testIterator.next() == 2)
        assert(testIterator.hasNext())
        testIterator.remove()
        assert(testIterator.next() == 3)
        assert(testIterator.hasNext())
        assert(testIterator.next() == 4)
        testIterator.remove()
        try
        {
            testIterator.remove()
            assert(false)
        }
        catch (ex:IllegalStateException)
        {
            // supposed to catch
        }
        assert(changes == listOf(
            ObservableIterator.Change(testIterator,2),
            ObservableIterator.Change(testIterator,4)
        ),{"changes: $changes"})
    }

    @Test
    fun next()
    {
        assert(testIterator.next() == 1)
        assert(testIterator.next() == 2)
        assert(testIterator.next() == 3)
        assert(testIterator.next() == 4)
        assert(testIterator.next() == 5)
        assert(changes.isEmpty(),{"changes: $changes"})
    }

    @Test
    fun hasHext()
    {
        assert(testIterator.hasNext())
        assert(testIterator.hasNext())
        assert(testIterator.hasNext())
        assert(testIterator.next() == 1)
        assert(testIterator.hasNext())
        assert(testIterator.hasNext())
        assert(testIterator.next() == 2)
        assert(testIterator.hasNext())
        assert(testIterator.next() == 3)
        assert(testIterator.hasNext())
        assert(testIterator.next() == 4)
        assert(testIterator.hasNext())
        assert(testIterator.next() == 5)
        assert(!testIterator.hasNext())
        assert(changes.isEmpty(),{"changes: $changes"})
    }
}
