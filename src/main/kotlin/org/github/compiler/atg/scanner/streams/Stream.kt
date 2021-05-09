package org.github.compiler.atg.scanner.streams

interface Stream<T> {
    fun next(): T
    fun peek(): T
    fun isEnded(): Boolean
}

fun Stream<*>.isNotEnded(): Boolean = !isEnded()
