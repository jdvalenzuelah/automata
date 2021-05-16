package org.github.compiler.atg.specification

import org.github.compiler.regularExpressions.regexImpl.IRegexDefinition
import org.github.compiler.regularExpressions.regexImpl.StateFulRegexDefinition
import org.github.compiler.regularExpressions.regexImpl.StatefulRegex
import org.github.compiler.atg.scanner.Scanner
import org.github.compiler.atg.scanner.streams.Stream
import org.github.compiler.atg.scanner.streams.toCharStream


data class TokenRef(val lexeme: String, val type: TokenType)

interface TokenType

object UnknownType : TokenType {
    override fun toString(): String = "UNKNOWN"
}

interface Token {
    val value: String
    val type: TokenType
}

interface Spec {

    fun getAllPatterns(): Map<TokenType, StatefulRegex>
    fun getAllKeywords(): Map<String, TokenType>
    fun ignoreSet(): Collection<Char>

    fun getScanner(source: String): Scanner = getScanner(source.toCharStream())

    fun getScanner(source: Stream<Char>): Scanner =
        Scanner(source, toRegexDefinition(), getAllKeywords(), ignoreSet())

    fun toRegexDefinition(): IRegexDefinition<TokenType> = StateFulRegexDefinition(getAllPatterns())

}
