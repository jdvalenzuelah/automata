package org.github.compiler.atg.scanner.streams

import java.io.Closeable
import java.io.File
import java.io.FileReader

class FileCharStream(
    private val source: FileReader
) : Stream<Char>, Closeable {

    constructor(source: File) : this(FileReader(source))

    private var lookahead = source.read()

    override fun isEnded(): Boolean = lookahead == -1

    override fun next(): Char {
        val next = lookahead
        lookahead = source.read()
        return next.toChar()
    }

    override fun peek(): Char = lookahead.toChar()

    override fun close() {
        source.close()
    }

}
