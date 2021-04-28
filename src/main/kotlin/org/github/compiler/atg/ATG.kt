package org.github.compiler.atg

import org.github.compiler.regularExpressions.Regex
import org.github.compiler.regularExpressions.regex.tokenize.escape

data class Character(val name: String, val def: String) {
    fun asRegexExpression() = def.toList().joinToString(separator = "|") { Regex.escape(it.toString()) }
}

data class Keyword(val name: String, val def: String)

data class TokenDef(val name: String, val regex: String)

data class ATG(
    val compilerName: String,
    val characters: Collection<Character>,
    val keywords: Collection<Keyword>,
    val tokens: Collection<TokenDef>,
    val ignoreSet: Character,
)
