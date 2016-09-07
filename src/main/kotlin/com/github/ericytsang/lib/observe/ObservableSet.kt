package com.github.ericytsang.lib.observe

interface ObservableSet<V>:MutableSet<V>,KeylessChange.Observable<V>
