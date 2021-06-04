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

sealed class SymbolType {
    data class Literal(val def: String): SymbolType()
    data class Ident(override val name: String): SymbolType(), Identifiable
}

/* Always | if size > 1*/
data class Expression(val expr: MutableList<Term> = mutableListOf())

/* Always concat if size > 1 */
data class Term(val factors: MutableList<Factor> = mutableListOf())

sealed class Factor {
    data class Symbol(var symbol: SymbolType? = null, val attrs: MutableList<Token> = mutableListOf()) : Factor() {
        val attrsCode : String
            get() = attrs.joinToString(separator = "") { it.lexeme }
    }
    data class Grouped(val expr: Expression = Expression()) : Factor()
    data class Optional(val expr: Expression = Expression()) : Factor()
    data class Repeat(val expr: Expression = Expression()) : Factor()
    data class SemAction(val semAction: MutableList<Token>) : Factor() {
        val code : String
            get() = semAction.joinToString(separator = "") { it.lexeme }
    }
}

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

fun ATG.isKnownToken(symbol: SymbolType.Ident): Boolean = symbol.name in knownTokens
