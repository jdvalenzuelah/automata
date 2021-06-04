package org.github.compiler.atg.scanner.streams

data class ArrayDequeStream<T>(
    val data: ArrayDeque<T>
): Stream<T> {

    override fun isEnded(): Boolean = data.isEmpty()

    override fun next(): T = data.removeFirst()

    override fun peek(): T = data.first()

}
