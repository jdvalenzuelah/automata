package org.github.compiler.atg.scanner

import org.github.compiler.atg.scanner.streams.CharStream
import org.github.compiler.atg.scanner.streams.Stream
import org.github.compiler.atg.scanner.streams.isNotEnded
import org.github.compiler.atg.specification.TokenRef
import org.github.compiler.atg.specification.TokenType
import org.github.compiler.regularExpressions.regexImpl.IRegexDefinition

class Scanner(
    private val source: Stream<Char>,
    private val definition: IRegexDefinition<TokenType>,
    private val keywords: Map<String, TokenType>,
    private val ignoreSet: Collection<Char>
): Stream<TokenRef> {

    private val scannedTokens = mutableListOf<TokenRef>()

    private var currentMatch: String = ""

    private var backtrackStream = CharStream("")

    fun scanTokens(): Collection<TokenRef> {
        while (isNotEnded())
            nextToken()

        return scannedTokens
    }

    private fun nextToken() {
        val matches = mutableListOf<Pair<String, TokenType>>()
        while (isNotEnded() && definition.hasNext(peekChar())) {
            val next = nextChar()
            currentMatch += next
            definition.move(next)

            if(definition.isAccepted()) {
                matches.add(currentMatch to definition.getResult()!!)
            }

        }

        val (match, matchType) = if(definition.isAccepted()) {
            currentMatch to definition.getResult()!!
        } else {
            matches.maxByOrNull { it.first.length }
        } ?: error("unrecognized token $currentMatch!")


        if(match == currentMatch) {
            addToken(matchType)
        } else {
            backtrackStream = CharStream(currentMatch.removePrefix(match))
            currentMatch = match
            addToken(matchType)
            clean()
            return nextToken()
        }

        clean()
    }

    private fun addToken(type: TokenType) {
        val typeToSave = keywords[currentMatch] ?: type
        scannedTokens.add(TokenRef(currentMatch, typeToSave))
    }

    private fun clean() {
        definition.reset()
        currentMatch = ""
        backtrackStream = CharStream("")
    }

    private fun nextChar(): Char {
        if(backtrackStream.isNotEnded())
            return backtrackStream.next()

        var next = source.next()
        while (next in ignoreSet)
            next = source.next()
        return next
    }

    private fun peekChar(): Char {
        if(backtrackStream.isNotEnded())
            return backtrackStream.peek()

        var next = source.peek()
        while (next in ignoreSet) {
            source.next() //ignore
            next = source.peek()
        }
        return next
    }

    private fun isNotEnded(): Boolean = try { peekChar(); true } catch (e: Exception) { false }

    override fun next(): TokenRef {
        if(scannedTokens.isNotEmpty())
            return scannedTokens.removeFirst()

        nextToken()
        return scannedTokens.removeFirst()
    }

    override fun peek(): TokenRef {
        if(scannedTokens.isNotEmpty())
            return scannedTokens.first()

        nextToken()
        return scannedTokens.first()
    }

    override fun isEnded(): Boolean = !isNotEnded() || scannedTokens.isNotEmpty()
}
