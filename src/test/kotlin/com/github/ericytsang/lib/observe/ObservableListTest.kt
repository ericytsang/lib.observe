package com.github.ericytsang.lib.observe

import org.junit.Test

/**
 * Created by surpl on 8/21/2016.
 */
class ObservableListTest
{
    val changes:MutableList<KeyedChange<Int,Int>> = mutableListOf()

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
            KeyedChange(testList,listOf(4).mapIndexed {i,e -> i+3 to e}.toMap(),listOf(9).mapIndexed {i,e -> i+3 to e}.toMap())
        ),{"changes: $changes"})
    }

    @Test
    fun addBulk()
    {
        testList.addAll(listOf(5,3,5,6))
        assert(testList == listOf(1,2,3,4,5,5,3,5,6),{"testList: $testList"})
        assert(changes == listOf(
            KeyedChange(testList,emptyMap(),listOf(5,3,5,6).mapIndexed {i,e -> i+5 to e}.toMap())
        ),{"changes: $changes"})
    }

    @Test
    fun addBulkAt()
    {
        testList.addAll(2,listOf(5,3,5,6))
        assert(testList == listOf(1,2,5,3,5,6,3,4,5),{"testList: $testList"})
        assert(changes == listOf(
            KeyedChange(testList,emptyMap(),listOf(5,3,5,6).mapIndexed {i,e -> i+2 to e}.toMap())
        ),{"changes: $changes"})
    }

    @Test
    fun addAtIndex()
    {
        testList.add(3,2)
        testList.add(4,3)
        assert(testList == listOf(1,2,3,2,3,4,5),{"testList: $testList"})
        assert(changes == listOf(
            KeyedChange(testList,emptyMap(),listOf(2).mapIndexed {i,e -> i+3 to e}.toMap()),
            KeyedChange(testList,emptyMap(),listOf(3).mapIndexed {i,e -> i+4 to e}.toMap())
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
            KeyedChange(testList,emptyMap(),listOf(2).mapIndexed {i,e -> i+5 to e}.toMap()),
            KeyedChange(testList,emptyMap(),listOf(3).mapIndexed {i,e -> i+6 to e}.toMap()),
            KeyedChange(testList,emptyMap(),listOf(4).mapIndexed {i,e -> i+7 to e}.toMap())
        ),{"changes: $changes"})
    }

    @Test
    fun removeBulk()
    {
        testList.removeAll(listOf(5,3,5,6))
        assert(testList == listOf(1,2,4),{"testList: $testList"})
        assert(changes == listOf(
            KeyedChange(testList,listOf(5).mapIndexed {i,e -> i+4 to e}.toMap(),emptyMap()),
            KeyedChange(testList,listOf(3).mapIndexed {i,e -> i+2 to e}.toMap(),emptyMap())
        ),{"changes: $changes"})
    }

    @Test
    fun removeAt()
    {
        assert(testList.removeAt(2) == 3)
        assert(testList.removeAt(3) == 5)
        assert(testList == listOf(1,2,4),{"testList: $testList"})
        assert(changes == listOf(
            KeyedChange(testList,listOf(3).mapIndexed {i,e -> i+2 to e}.toMap(),emptyMap()),
            KeyedChange(testList,listOf(5).mapIndexed {i,e -> i+3 to e}.toMap(),emptyMap())
        ),{"changes: $changes"})
    }

    @Test
    fun removeElement()
    {
        testList.remove(2)
        testList.remove(4)
        assert(testList == listOf(1,3,5),{"testList: $testList"})
        assert(changes == listOf(
            KeyedChange(testList,listOf(2).mapIndexed {i,e -> i+1 to e}.toMap(),emptyMap()),
            KeyedChange(testList,listOf(4).mapIndexed {i,e -> i+2 to e}.toMap(),emptyMap())
        ),{"changes: $changes"})
    }

    @Test
    fun removeClear()
    {
        testList.clear()
        assert(testList.isEmpty(),{"testList: $testList"})
        assert(changes == listOf(
            KeyedChange(testList,listOf(1,2,3,4,5).mapIndexed {i,e -> i+0 to e}.toMap(),emptyMap())
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
            KeyedChange(testList,listOf(2,3).mapIndexed {i,e -> i+1 to e}.toMap(),emptyMap())
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
            KeyedChange(testList,listOf(2,3,4).mapIndexed {i,e -> i+1 to e}.toMap(),emptyMap())
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
            KeyedChange(testList,emptyMap(),listOf(6).mapIndexed {i,e -> i+3 to e}.toMap()),
            KeyedChange(testList,emptyMap(),listOf(7).mapIndexed {i,e -> i+4 to e}.toMap()),
            KeyedChange(testList,emptyMap(),listOf(8).mapIndexed {i,e -> i+5 to e}.toMap())
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
            KeyedChange(testList,listOf(3).mapIndexed {i,e -> i+2 to e}.toMap(),emptyMap()),
            KeyedChange(testList,listOf(2).mapIndexed {i,e -> i+1 to e}.toMap(),emptyMap())
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
            KeyedChange(testList,listOf(3).mapIndexed {i,e -> i+2 to e}.toMap(),listOf(10).mapIndexed {i,e -> i+2 to e}.toMap()),
            KeyedChange(testList,listOf(4).mapIndexed {i,e -> i+3 to e}.toMap(),listOf(10).mapIndexed {i,e -> i+3 to e}.toMap())
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
            KeyedChange(testList,emptyMap(),listOf(4).mapIndexed {i,e -> i+3 to e}.toMap()),
            KeyedChange(testList,emptyMap(),listOf(8).mapIndexed {i,e -> i+5 to e}.toMap())
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
            KeyedChange(testList,listOf(3).mapIndexed {i,e -> i+2 to e}.toMap(),emptyMap()),
            KeyedChange(testList,listOf(4).mapIndexed {i,e -> i+2 to e}.toMap(),emptyMap())
        ),{"changes: $changes"})
    }
}
