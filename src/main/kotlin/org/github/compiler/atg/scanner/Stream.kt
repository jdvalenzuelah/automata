package org.github.compiler.atg.scanner

import org.github.compiler.regularExpressions.transforms.Transform


interface Stream<T> {
    fun next(): T
    fun peek(): T
    fun isEnded(): Boolean
}

fun Stream<*>.isNotEnded(): Boolean = !isEnded()

class CharStream(
    source: String
) : Stream<Char> {

    private val queue = ArrayDeque(source.toList())

    override fun isEnded(): Boolean = queue.isEmpty()

    override fun next(): Char = queue.removeFirst()

    override fun peek(): Char = queue.first()

}

private val stringToCharStream = Transform<String, Stream<Char>> { CharStream(it) }

fun String.toCharStream() = stringToCharStream(this)
