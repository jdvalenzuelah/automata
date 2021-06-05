package org.github.compiler.atg.specification

import org.github.compiler.regularExpressions.regexImpl.IRegexDefinition
import org.github.compiler.regularExpressions.regexImpl.StateFulRegexDefinition
import org.github.compiler.regularExpressions.regexImpl.StatefulRegex
import org.github.compiler.atg.scanner.Scanner
import org.github.compiler.atg.scanner.streams.FileCharStream
import org.github.compiler.atg.scanner.streams.Stream
import org.github.compiler.atg.scanner.streams.toCharStream
import java.io.File


interface IToken {
    val lexeme: String
    val type: TokenType
}

interface TokenType

data class TokenRef(override val lexeme: String, override val type: TokenType): IToken

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

    fun getScanner(file: File): Scanner = Scanner(FileCharStream(file), toRegexDefinition(), getAllKeywords(), ignoreSet())

    fun toRegexDefinition(): IRegexDefinition<TokenType> = StateFulRegexDefinition(getAllPatterns())

    fun parse(source: Stream<TokenRef>)

}
