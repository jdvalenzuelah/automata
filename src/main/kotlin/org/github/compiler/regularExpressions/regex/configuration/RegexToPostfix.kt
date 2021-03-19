package org.github.compiler.regularExpressions.regex.configuration

import org.github.compiler.regularExpressions.regex.RegularExpression
import org.github.compiler.regularExpressions.regex.transform.RegexTransforms
import org.github.compiler.regularExpressions.regex.transform.postfix.InfixRegexToPostfix

/*
 * Implementation of regex transform from infix to postfix notation
 */
object RegexToPostfix {

    operator fun invoke(): RegexTransforms<RegularExpression> {
        return InfixRegexToPostfix
    }

}
