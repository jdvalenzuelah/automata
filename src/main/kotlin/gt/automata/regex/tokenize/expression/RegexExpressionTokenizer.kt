package gt.automata.regex.tokenize.expression

import gt.automata.regex.RegexExpression
import gt.automata.regex.element.Character
import gt.automata.regex.element.Grouping
import gt.automata.regex.element.Operator
import gt.automata.regex.element.RegexElement
import gt.automata.regex.tokenize.element.TokenizeRegexElement
import gt.automata.regex.tokenize.isCharacter

class RegexExpressionTokenizer(
    private val operatorTokenizer: TokenizeRegexElement<Operator>,
    private val groupingTokenizer: TokenizeRegexElement<Grouping>,
    private val characterTokenizer: TokenizeRegexElement<Character>,
) : TokenizeRegex {


    private fun getRegexElement(str: String): RegexElement {
        val operator = operatorTokenizer(str)
        if(operator != null) return operator

        val grouping = groupingTokenizer(str)
        if(grouping != null) return grouping

        return characterTokenizer(str)!!

    }

    override fun invoke(regex: String): RegexExpression {
        val result = mutableListOf<RegexElement>()
        var lastElement: RegexElement? = null

        regex.forEach {
            val currentElement = getRegexElement(it.toString())

            if(lastElement.isCharacter() && currentElement.isCharacter()) { // Two characters in a row, infers concatenation
                result.add(Operator.Concatenation)
            }

            result.add(currentElement)
            lastElement = currentElement
        }

        return result
    }

}