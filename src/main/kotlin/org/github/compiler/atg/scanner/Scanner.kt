package org.github.compiler.atg.scanner

import org.github.compiler.atg.specification.TokenRef
import org.github.compiler.atg.specification.TokenType
import org.github.compiler.regularExpressions.regexImpl.IRegexDefinition

class Scanner(
    private val source: Stream<Char>,
    private val definition: IRegexDefinition<TokenType>,
    private val keywords: Map<String, TokenType>,
    private val ignoreSet: Collection<Char>
) {

    private val scannedTokens = mutableListOf<TokenRef>()

    private var currentMatch: String = ""

    fun scanTokens(): Collection<TokenRef> {
        while (isNotEnded())
            nextToken()

        return scannedTokens
    }

    private fun nextToken() {
        while (isNotEnded() && definition.hasNext(peek())) {
            val next = next()
            currentMatch += next
            definition.move(next)
        }
        val result = definition.getResult()
            ?: error("unrecognized token $currentMatch!")

        addToken(result)
        clean()
    }

    private fun addToken(type: TokenType) {
        val typeToSave = keywords[currentMatch] ?: type
        scannedTokens.add(TokenRef(currentMatch, typeToSave))
    }

    private fun clean() {
        definition.reset()
        currentMatch = ""
    }

    private fun next(): Char {
        var next = source.next()
        while (next in ignoreSet)
            next = source.next()
        return next
    }

    private fun peek(): Char {
        var next = source.peek()
        while (next in ignoreSet) {
            source.next() //ignore
            next = source.peek()
        }
        return next
    }

    private fun isNotEnded(): Boolean = try { peek(); true } catch (e: Exception) { false }
}
