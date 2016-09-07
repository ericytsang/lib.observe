package com.github.ericytsang.lib.observe

interface ObservableList<E>:MutableList<E>,KeyedChange.Observable<Int,E>
