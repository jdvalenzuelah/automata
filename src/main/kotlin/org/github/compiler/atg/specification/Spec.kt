package org.github.compiler.atg.specification

import org.github.compiler.regularExpressions.regexImpl.StatefulRegex

interface TokenType

interface Token {
    val value: String
    val type: TokenType
}

interface Spec {

    fun getAllPatterns(): Map<TokenType, StatefulRegex>
    fun getKeyword(lexeme: String): TokenType?

}
