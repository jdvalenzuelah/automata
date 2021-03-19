package org.github.compiler.regularExpressions.regex.tokenize.expression

import org.github.compiler.regularExpressions.regex.RegularExpression
import org.github.compiler.regularExpressions.regex.RegexTokenizer
import org.github.compiler.regularExpressions.regex.elements.Augmented
import org.github.compiler.regularExpressions.regex.elements.Character
import org.github.compiler.regularExpressions.regex.elements.Grouping
import org.github.compiler.regularExpressions.regex.elements.Grouping.*
import org.github.compiler.regularExpressions.regex.elements.Operator
import org.github.compiler.regularExpressions.regex.elements.Operator.*
import org.github.compiler.regularExpressions.regex.elements.RegexElement
import org.github.compiler.regularExpressions.regex.tokenize.element.RegexElementTokenizer
import org.github.compiler.regularExpressions.regex.tokenize.isCharacter

class TokenizeRegex(
    private val operator: RegexElementTokenizer<Operator>,
    private val grouping: RegexElementTokenizer<Grouping>,
    private val augmented: RegexElementTokenizer<Augmented>,
    private val character: RegexElementTokenizer<Character>
) : RegexTokenizer {

    private fun getRegexElement(char: Char): RegexElement {
        val operator = operator(char)
        if(operator != null) return operator

        val grouping = grouping(char)
        if(grouping != null) return grouping

        val augmented = augmented(char)
        if(augmented != null) return augmented

        return character(char)!!
    }

    private fun shouldAddConcatenation(current: RegexElement, previous:RegexElement?): Boolean {
        return when(current) {
            is Character, is Augmented -> previous.isCharacter() || previous == CloseParenthesis || previous == Closure
            OpenParenthesis -> previous.isCharacter() || previous in listOf(Closure, PositiveClosure, ZeroOrOne, CloseParenthesis)
            else -> false
        }
    }

    override fun invoke(regex: String): RegularExpression {
        val result = mutableListOf<RegexElement>()
        var lastElement: RegexElement? = null

        regex.forEach {
            val currentElement = getRegexElement(it)

            if(shouldAddConcatenation(currentElement, lastElement)) {
                result.add(Concatenation)
            }

            result.add(currentElement)
            lastElement = currentElement
        }

        return result
    }

}
