package gt.regex.tokenize.configuration

import gt.regex.tokenize.element.AugmentedTokenizer
import gt.regex.tokenize.element.CharacterTokenizer
import gt.regex.tokenize.element.GroupingTokenizer
import gt.regex.tokenize.element.OperatorTokenizer
import gt.regex.tokenize.expression.RegexExpressionTokenizer
import gt.regex.tokenize.expression.TokenizeRegex

object TokenizerConfig {

    fun expressionTokenizer(): TokenizeRegex {
        return RegexExpressionTokenizer(OperatorTokenizer, GroupingTokenizer, CharacterTokenizer, AugmentedTokenizer)
    }

}
