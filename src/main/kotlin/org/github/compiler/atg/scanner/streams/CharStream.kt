package org.github.compiler.atg.scanner.streams

class CharStream(
    source: String
) : Stream<Char> {

    private val queue = ArrayDeque(source.toList())

    override fun isEnded(): Boolean = queue.isEmpty()

    override fun next(): Char = queue.removeFirst()

    override fun peek(): Char = queue.first()

}
