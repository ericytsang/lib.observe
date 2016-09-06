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
    val changes:MutableList<KeyedChange<String,Int>> = mutableListOf()

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
            KeyedChange(testMap,emptyMap(),mapOf("q" to 7)),
            KeyedChange(testMap,emptyMap(),mapOf("w" to 9)),
            KeyedChange(testMap,emptyMap(),mapOf("e" to 4)),
            KeyedChange(testMap,mapOf("a" to 1),mapOf("a" to 1)),
            KeyedChange(testMap,mapOf("b" to 2),mapOf("b" to 0)),
            KeyedChange(testMap,mapOf("c" to 3),mapOf("c" to -2)),
            KeyedChange(testMap,mapOf("e" to 4),emptyMap()),
            KeyedChange(testMap,mapOf("a" to 1),emptyMap())
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
            KeyedChange(testMap,emptyMap(),mapOf("q" to 7)),
            KeyedChange(testMap,emptyMap(),mapOf("w" to 9)),
            KeyedChange(testMap,emptyMap(),mapOf("e" to 4))
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
            KeyedChange(testMap,mapOf("a" to 1),mapOf("a" to 1)),
            KeyedChange(testMap,mapOf("b" to 2),mapOf("b" to 0)),
            KeyedChange(testMap,mapOf("c" to 3),mapOf("c" to -2))
        )
            ,{"changes: $changes"})
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
            KeyedChange(testMap,mapOf("a" to 1),emptyMap()),
            KeyedChange(testMap,mapOf("b" to 2),emptyMap())
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
            KeyedChange(testMap,mapOf("a" to 1,"b" to 2,"c" to 3),emptyMap())
        )),{"changes: $changes"})
        assert(testMap.isEmpty(),{"testMap: $testMap"})
    }
}
