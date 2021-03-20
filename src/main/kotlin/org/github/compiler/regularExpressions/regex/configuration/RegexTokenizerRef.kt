package org.github.compiler.regularExpressions.regex.configuration

import org.github.compiler.regularExpressions.regex.RegexTokenizer
import org.github.compiler.regularExpressions.regex.tokenize.expression.TokenizeRegex
import org.github.compiler.regularExpressions.regex.tokenize.element.*
import org.github.compiler.regularExpressions.transforms.Transform

/*
 * Regex tokenizer implementation using reflection character tokenizers
 */
object RegexTokenizerRef {

    operator fun invoke(): RegexTokenizer {
        return TokenizeRegex(TokenizeOperator, TokenizeGrouping, TokenizeAugmented, TokenizeCharacter)
    }

}
