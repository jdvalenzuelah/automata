package gt.automata.regex.tokenize.expression

import gt.automata.regex.RegexExpression

fun interface TokenizeRegex : (String) -> RegexExpression
