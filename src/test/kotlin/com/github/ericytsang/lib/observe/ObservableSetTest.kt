package com.github.ericytsang.lib.observe

import org.junit.Test

/**
 * Created by surpl on 8/20/2016.
 */
class ObservableSetTest
{
    /**
     * list of elements added to [testSet] in chronological order.
     */
    val addedElements:MutableList<Int> = mutableListOf()

    /**
     * list of elements removed from [testSet] in chronological order.
     */
    val removedElements:MutableList<Int> = mutableListOf()

    /**
     * the observed set to test...
     */
    val testSet = ObservableSet(mutableSetOf(1,2,3,4,5)).apply()
    {
        observers += KeylessChange.Observer.new()
        {
            change ->
            when (change.action)
            {
                KeylessChange.Action.ADD -> addedElements.add(change.value)
                KeylessChange.Action.REMOVE -> removedElements.add(change.value)
            }
        }
    }

    @Test
    fun generalTest()
    {
        testSet.add(7)      // added
        testSet.add(9)      // added
        testSet.add(4)      // cannot add existing element
        testSet.add(1)      // cannot add existing element
        testSet.add(0)      // added
        testSet.add(-2)     // added
        testSet.remove(4)   // removed
        testSet.remove(1)   // removed
        testSet.remove(-1)  // cannot remove non-existent element
        assert(addedElements == listOf(7,9,0,-2),{"addedElements: $addedElements"})
        assert(removedElements == listOf(4,1),{"removedElements: $removedElements"})
        assert(testSet == setOf(2,3,5,7,9,0,-2),{"testSet: $testSet"})
    }

    @Test
    fun addNonExistentTest()
    {
        testSet.add(7)
        testSet.add(9)
        testSet.add(0)
        testSet.add(-2)
        assert(addedElements == listOf(7,9,0,-2),{"addedElements: $addedElements"})
        assert(removedElements.isEmpty(),{"removedElements: $removedElements"})
        assert(testSet == setOf(1,2,3,4,5,7,9,0,-2),{"testSet: $testSet"})
    }

    @Test
    fun addExistingTest()
    {
        testSet.add(4)
        testSet.add(1)
        assert(addedElements.isEmpty(),{"addedElements: $addedElements"})
        assert(removedElements.isEmpty(),{"removedElements: $removedElements"})
        assert(testSet == setOf(1,2,3,4,5),{"testSet: $testSet"})
    }

    @Test
    fun removeExistingTest()
    {
        testSet.remove(4)
        testSet.remove(1)
        assert(addedElements.isEmpty(),{"addedElements: $addedElements"})
        assert(removedElements == listOf(4,1),{"removedElements: $removedElements"})
        assert(testSet == setOf(2,3,5),{"testSet: $testSet"})
    }

    @Test
    fun removeNonExistentTest()
    {
        testSet.remove(-1)
        testSet.remove(-2)
        testSet.remove(-1)
        assert(addedElements.isEmpty(),{"addedElements: $addedElements"})
        assert(removedElements.isEmpty(),{"removedElements: $removedElements"})
        assert(testSet == setOf(1,2,3,4,5),{"testSet: $testSet"})
    }

    @Test
    fun clearTest()
    {
        testSet.clear()
        assert(addedElements.isEmpty(),{"addedElements: $addedElements"})
        assert(removedElements.containsAll(listOf(1,2,3,4,5)),{"removedElements: $removedElements"})
        assert(testSet.isEmpty(),{"testSet: $testSet"})
    }

    @Test
    fun iteratorTest()
    {
        run()
        {
            val it = testSet.iterator()
            assert(it.next() == 1)
            assert(it.next() == 2)
            assert(it.hasNext())
            it.remove()
            assert(it.next() == 3)
            assert(it.hasNext())
            assert(it.next() == 4)
            it.remove()
            assert(it.next() == 5)
            it.remove()
            assert(!it.hasNext())
        }
        run()
        {
            val it = testSet.iterator()
            assert(it.next() == 1)
            assert(it.hasNext())
            assert(it.next() == 3)
            assert(!it.hasNext())
        }
        assert(addedElements.isEmpty(),{"addedElements: $addedElements"})
        assert(removedElements == listOf(2,4,5),{"removedElements: $removedElements"})
        assert(testSet == setOf(1,3),{"testSet: $testSet"})
    }
}
