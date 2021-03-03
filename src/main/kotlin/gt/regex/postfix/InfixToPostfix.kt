package gt.regex.postfix

import gt.regex.RegexExpression

// TODO: Determine if further abstraction is needed
fun interface InfixToPostfix: (RegexExpression) -> RegexExpression
