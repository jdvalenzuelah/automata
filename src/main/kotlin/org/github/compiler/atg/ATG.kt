package org.github.compiler.atg

import org.github.compiler.regularExpressions.Regex
import org.github.compiler.regularExpressions.regex.tokenize.escape

interface Identifiable {
    val name: String
}

data class Character(override val name: String, val def: String): Identifiable {
    fun asRegexExpression() = def.toList().joinToString(separator = "|") { Regex.escape(it.toString()) }
}

data class Keyword(override val name: String, val def: String): Identifiable

data class TokenDef(override val name: String, val regex: String): Identifiable

sealed class Symbol {
    data class Literal(val def: String): Symbol()
    data class Ident(override val name: String): Symbol(), Identifiable
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
    val code: List<String>,
) {
    internal val knownTokens by lazy {
        characters.map { it.name } + tokens.map { it.name } + keywords.map { it.name }
    }
}

fun ATG.isKnownToken(symbol: Symbol.Ident): Boolean = symbol.name in knownTokens
