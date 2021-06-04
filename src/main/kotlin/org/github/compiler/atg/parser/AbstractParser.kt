package org.github.compiler.atg.parser

import org.github.compiler.atg.scanner.streams.Stream
import org.github.compiler.atg.scanner.streams.isNotEnded
import org.github.compiler.atg.specification.IToken
import org.github.compiler.atg.specification.TokenRef
import org.github.compiler.atg.specification.TokenType
import org.tinylog.kotlin.Logger

abstract class AbstractParser<T: IToken, R>(
    private val tokens: Stream<T>
) {

    data class SynError(val message: String)

    protected var lookAhead: T = tokens.next()
    protected var lastToken: T? = null

    val errors = mutableListOf<SynError>()

    abstract fun parse(): R

    private fun next() {
        lastToken = lookAhead
        lookAhead = tokens.next()
    }

    protected fun hasNext(): Boolean = tokens.isNotEnded()

    protected fun expect(type: TokenType) {
        if(lookAhead.type == type)
            next()
        else
            synError("Expected $type got $lookAhead")

    }

    protected fun expectLiteral(literal: String) {
        if(lookAhead.lexeme == literal)
            next()
        else
            synError("Expected $literal got $lookAhead")

    }

    protected fun synError(message: String) {
        Logger.error("SynError: $message")
        errors.add(SynError(message))
    }

}
