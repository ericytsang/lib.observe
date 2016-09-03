package com.github.ericytsang.lib.observe

import org.junit.Test

/**
 * Created by surpl on 8/20/2016.
 */
class ObservableMapTest
{
    /**
     * list of changes passed to listener of [testMap] in chronological order.
     */
    val changes:MutableList<KeyedChange<ObservableMap<String,Int>,String,Int>> = mutableListOf()

    /**
     * the observed set to test...
     */
    val testMap = ObservableMap(mutableMapOf("a" to 1,"b" to 2,"c" to 3)).apply()
    {
        observers += KeyedChange.Observer.new()
        {
            changes.add(it)
        }
    }

    @Test
    fun generalTest()
    {
        testMap.put("q",7)
        testMap.put("w",9)
        testMap.put("e",4)
        testMap.put("a",1)
        testMap.put("b",0)
        testMap.put("c",-2)
        testMap.remove("e")
        testMap.remove("a")
        testMap.remove("z")
        assert(changes == listOf(
            KeyedChange.newAdd(testMap,"q",7),
            KeyedChange.newAdd(testMap,"w",9),
            KeyedChange.newAdd(testMap,"e",4),
            KeyedChange.newSet(testMap,"a",1,1),
            KeyedChange.newSet(testMap,"b",2,0),
            KeyedChange.newSet(testMap,"c",3,-2),
            KeyedChange.newRemove(testMap,"e",4),
            KeyedChange.newRemove(testMap,"a",1)
        ),{"changes: $changes"})
        assert(testMap == mapOf(
            "b" to 0,
            "c" to -2,
            "q" to 7,
            "w" to 9
        ),{"testMap: $testMap"})
    }

    @Test
    fun putNonExistentTest()
    {
        testMap.put("q",7)
        testMap.put("w",9)
        testMap.put("e",4)
        assert(changes == listOf(
            KeyedChange.newAdd(testMap,"q",7),
            KeyedChange.newAdd(testMap,"w",9),
            KeyedChange.newAdd(testMap,"e",4)
        ),{"changes: $changes"})
        assert(testMap == mapOf(
            "a" to 1,
            "b" to 2,
            "c" to 3,
            "q" to 7,
            "w" to 9,
            "e" to 4
        ),{"testMap: $testMap"})
    }

    @Test
    fun putExistingTest()
    {
        testMap.put("a",1)
        testMap.put("b",0)
        testMap.put("c",-2)
        assert(changes == listOf(
            KeyedChange.newSet(testMap,"a",1,1),
            KeyedChange.newSet(testMap,"b",2,0),
            KeyedChange.newSet(testMap,"c",3,-2)
        ),{"changes: $changes"})
        assert(testMap == mapOf(
            "a" to 1,
            "b" to 0,
            "c" to -2
        ),{"testMap: $testMap"})
    }

    @Test
    fun removeExistingTest()
    {
        testMap.remove("a")
        testMap.remove("b")
        assert(changes == listOf(
            KeyedChange.newRemove(testMap,"a",1),
            KeyedChange.newRemove(testMap,"b",2)
        ),{"changes: $changes"})
        assert(testMap == mapOf(
            "c" to 3
        ),{"testMap: $testMap"})
    }

    @Test
    fun removeNonExistentTest()
    {
        testMap.remove("z")
        assert(changes.isEmpty(),{"changes: $changes"})
        assert(testMap == mapOf(
            "a" to 1,
            "b" to 2,
            "c" to 3
        ),{"testMap: $testMap"})
    }

    @Test
    fun clearTest()
    {
        testMap.clear()
        assert(changes.containsAll(listOf(
            KeyedChange.newRemove(testMap,"a",1),
            KeyedChange.newRemove(testMap,"b",2),
            KeyedChange.newRemove(testMap,"c",3)
        )),{"changes: $changes"})
        assert(testMap.isEmpty(),{"testMap: $testMap"})
    }
}
