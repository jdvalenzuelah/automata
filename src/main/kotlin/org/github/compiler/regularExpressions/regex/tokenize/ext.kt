package org.github.compiler.regularExpressions.regex.tokenize

import org.github.compiler.regularExpressions.Regex
import org.github.compiler.regularExpressions.regex.elements.*
import org.github.compiler.regularExpressions.regex.tokenize.element.TokenizeAugmented
import org.github.compiler.regularExpressions.regex.tokenize.element.TokenizeGrouping
import org.github.compiler.regularExpressions.regex.tokenize.element.TokenizeOperator

fun RegexElement?.isOperator() = this != null && this is Operator

fun RegexElement?.isGrouping() = this != null && this is Grouping

fun RegexElement?.isCharacter() = this != null && (this is Character || this is Augmented)

fun Regex.Companion.escape(ch: String): String {
    return ch.toList().joinToString(separator = "") {
        val isReserved = TokenizeOperator(it) != null || TokenizeGrouping(it) != null || TokenizeAugmented(it) != null || ch == "\\"
        if(isReserved) "\\$it" else it.toString()
    }
}
