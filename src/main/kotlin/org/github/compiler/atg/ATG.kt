package org.github.compiler.atg

import org.github.compiler.regularExpressions.Regex
import org.github.compiler.regularExpressions.regex.tokenize.escape

data class Character(val name: String, val def: String) {
    fun asRegexExpression() = def.toList().joinToString(separator = "|") { Regex.escape(it.toString()) }
}

data class Keyword(val name: String, val def: String)

data class TokenDef(val name: String, val regex: String)

sealed class Symbol {
    data class Literal(val def: String): Symbol()
    data class Ident(val name: String): Symbol()
}

sealed class Factor {

    data class Optional(val expr: Expression) : Factor()
    data class Repeat(val expr: Expression) : Factor()
    data class Grouped(val expr: Expression) : Factor()
    data class SemAction(val semanticAction: String) : Factor()
    data class Simple(val symbol: Symbol, val attributes: String?) : Factor()

}

typealias Term = Collection<Factor>

typealias Expression = Collection<Term>

data class Production(
    val name: String,
    val attributes: String,
    val semanticAction: String,
    val expression: Expression
)

data class ATG(
    val compilerName: String,
    val characters: Collection<Character>,
    val keywords: Collection<Keyword>,
    val tokens: Collection<TokenDef>,
    val ignoreSet: Character,
    val productions: Collection<Production>,
)
