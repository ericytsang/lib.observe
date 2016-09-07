package com.github.ericytsang.lib.observe

/**
 * Created by surpl on 6/21/2016.
 */
interface ObservableMap<K,V>:MutableMap<K,V>,KeyedChange.Observable<K,V>
