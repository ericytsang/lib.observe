package com.github.ericytsang.lib.observe

interface ObservableListIterator<E>:MutableListIterator<E>,KeyedChange.Observable<Int,E>
