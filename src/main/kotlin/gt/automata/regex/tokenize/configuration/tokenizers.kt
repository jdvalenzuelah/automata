package gt.automata.regex.tokenize.configuration

import gt.automata.regex.tokenize.element.CharacterTokenizer
import gt.automata.regex.tokenize.element.GroupingTokenizer
import gt.automata.regex.tokenize.element.OperatorTokenizer
import gt.automata.regex.tokenize.expression.RegexExpressionTokenizer
import gt.automata.regex.tokenize.expression.TokenizeRegex

object TokenizerConfig {

    fun expressionTokenizer(): TokenizeRegex {
        return RegexExpressionTokenizer(OperatorTokenizer, GroupingTokenizer, CharacterTokenizer)
    }

}
