package com.github.ericytsang.lib.observe

import org.junit.Test

/**
 * Created by surpl on 8/21/2016.
 */
class ObservableListTest
{
    val changes:MutableList<KeyedChange<ObservableList<Int>,IntRange,Collection<Int>>> = mutableListOf()

    val testList = ObservableList(mutableListOf(1,2,3,4,5)).apply()
    {
        observers += KeyedChange.Observer.new()
        {
            changes.add(it)
        }
    }

    @Test
    fun set()
    {
        testList[3] = 9
        assert(testList == listOf(1,2,3,9,5),{"testList: $testList"})
        assert(changes == listOf(
            KeyedChange.newSet(testList,3..3,listOf(4),listOf(9))
        ),{"changes: $changes"})
    }

    @Test
    fun addBulk()
    {
        testList.addAll(listOf(5,3,5,6))
        assert(testList == listOf(1,2,3,4,5,5,3,5,6),{"testList: $testList"})
        assert(changes == listOf(
            KeyedChange.newAdd(testList,5..8,listOf(5,3,5,6))
        ),{"changes: $changes"})
    }

    @Test
    fun addBulkAt()
    {
        testList.addAll(2,listOf(5,3,5,6))
        assert(testList == listOf(1,2,5,3,5,6,3,4,5),{"testList: $testList"})
        assert(changes == listOf(
            KeyedChange.newAdd(testList,2..5,listOf(5,3,5,6))
        ),{"changes: $changes"})
    }

    @Test
    fun addAtIndex()
    {
        testList.add(3,2)
        testList.add(4,3)
        assert(testList == listOf(1,2,3,2,3,4,5),{"testList: $testList"})
        assert(changes == listOf(
            KeyedChange.newAdd(testList,3..3,listOf(2)),
            KeyedChange.newAdd(testList,4..4,listOf(3))
        ),{"changes: $changes"})
    }

    @Test
    fun add()
    {
        testList.add(2)
        testList.add(3)
        testList.add(4)
        assert(testList == listOf(1,2,3,4,5,2,3,4),{"testList: $testList"})
        assert(changes == listOf(
            KeyedChange.newAdd(testList,5..5,listOf(2)),
            KeyedChange.newAdd(testList,6..6,listOf(3)),
            KeyedChange.newAdd(testList,7..7,listOf(4))
        ),{"changes: $changes"})
    }

    @Test
    fun removeBulk()
    {
        testList.removeAll(listOf(5,3,5,6))
        assert(testList == listOf(1,2,4),{"testList: $testList"})
        assert(changes == listOf(
            KeyedChange.newRemove(testList,4..4,listOf(5)),
            KeyedChange.newRemove(testList,2..2,listOf(3))
        ),{"changes: $changes"})
    }

    @Test
    fun removeAt()
    {
        assert(testList.removeAt(2) == 3)
        assert(testList.removeAt(3) == 5)
        assert(testList == listOf(1,2,4),{"testList: $testList"})
        assert(changes == listOf(
            KeyedChange.newRemove(testList,2..2,listOf(3)),
            KeyedChange.newRemove(testList,3..3,listOf(5))
        ),{"changes: $changes"})
    }

    @Test
    fun removeElement()
    {
        testList.remove(2)
        testList.remove(4)
        assert(testList == listOf(1,3,5),{"testList: $testList"})
        assert(changes == listOf(
            KeyedChange.newRemove(testList,1..1,listOf(2)),
            KeyedChange.newRemove(testList,2..2,listOf(4))
        ),{"changes: $changes"})
    }

    @Test
    fun removeClear()
    {
        testList.clear()
        assert(testList.isEmpty(),{"testList: $testList"})
        assert(changes == listOf(
            KeyedChange.newRemove(testList,0..4,listOf(1,2,3,4,5))
        ),{"changes: $changes"})
    }

    @Test
    fun sublistClear1()
    {
        val sublist = testList.subList(1,3)
        sublist.clear()
        assert(sublist.isEmpty(),{"sublist: $sublist"})
        assert(testList == listOf(1,4,5),{"testList: $testList"})
        assert(changes == listOf(
            KeyedChange.newRemove(testList,1..2,listOf(2,3))
        ),{"changes: $changes"})
    }

    @Test
    fun sublistClear2()
    {
        val sublist = testList.subList(1,4)
        sublist.clear()
        assert(sublist.isEmpty(),{"sublist: $sublist"})
        assert(testList == listOf(1,5),{"testList: $testList"})
        assert(changes == listOf(
            KeyedChange.newRemove(testList,1..3,listOf(2,3,4))
        ),{"changes: $changes"})
    }

    @Test
    fun sublistAdd()
    {
        val sublist = testList.subList(1,3)
        sublist.add(6)
        sublist.add(7)
        sublist.add(8)
        assert(sublist == listOf(2,3,6,7,8),{"sublist: $sublist"})
        assert(testList == listOf(1,2,3,6,7,8,4,5),{"testList: $testList"})
        assert(changes == listOf(
            KeyedChange.newAdd(testList,3..3,listOf(6)),
            KeyedChange.newAdd(testList,4..4,listOf(7)),
            KeyedChange.newAdd(testList,5..5,listOf(8))
        ),{"changes: $changes"})
    }

    @Test
    fun sublistRemove()
    {
        val sublist = testList.subList(1,3)
        sublist.remove(3)
        sublist.removeAt(0)
        assert(sublist.isEmpty(),{"sublist: $sublist"})
        assert(testList == listOf(1,4,5),{"testList: $testList"})
        assert(changes == listOf(
            KeyedChange.newRemove(testList,2..2,listOf(3)),
            KeyedChange.newRemove(testList,1..1,listOf(2))
        ),{"changes: $changes"})
    }

    @Test
    fun iteratorSet()
    {
        val iterator = testList.listIterator(2)
        iterator.next()
        iterator.set(10)
        iterator.next()
        iterator.set(10)
        assert(testList == listOf(1,2,10,10,5),{"testList: $testList"})
        assert(changes == listOf(
            KeyedChange.newSet(testList,2..2,listOf(3),listOf(10)),
            KeyedChange.newSet(testList,3..3,listOf(4),listOf(10))
        ),{"changes: $changes"})
    }

    @Test
    fun iteratorAdd()
    {
        val iterator = testList.listIterator(2)
        iterator.next()
        iterator.add(4)
        iterator.next()
        iterator.add(8)
        assert(testList == listOf(1,2,3,4,4,8,5),{"testList: $testList"})
        assert(changes == listOf(
            KeyedChange.newAdd(testList,3..3,listOf(4)),
            KeyedChange.newAdd(testList,5..5,listOf(8))
        ),{"changes: $changes"})
    }

    @Test
    fun iteratorRemove()
    {
        val iterator = testList.listIterator(2)
        iterator.next()
        iterator.remove()
        iterator.next()
        iterator.remove()
        assert(testList == listOf(1,2,5),{"testList: $testList"})
        assert(changes == listOf(
            KeyedChange.newRemove(testList,2..2,listOf(3)),
            KeyedChange.newRemove(testList,2..2,listOf(4))
        ),{"changes: $changes"})
    }
}
