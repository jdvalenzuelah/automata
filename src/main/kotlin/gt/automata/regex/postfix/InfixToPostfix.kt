package gt.automata.regex.postfix

import gt.automata.regex.RegexExpression

// TODO: Determine if further abstraction is needed
fun interface InfixToPostfix: (RegexExpression) -> RegexExpression
