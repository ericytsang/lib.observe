package com.github.ericytsang.lib.observe

interface ObservableIterator<E>:MutableIterator<E>,KeylessChange.Observable<E>
