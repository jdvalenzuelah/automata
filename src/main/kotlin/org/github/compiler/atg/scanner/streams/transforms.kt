package org.github.compiler.atg.scanner.streams

import org.github.compiler.regularExpressions.transforms.Transform

private val stringToCharStream = Transform<String, Stream<Char>> { CharStream(it) }

fun String.toCharStream() = stringToCharStream(this)
