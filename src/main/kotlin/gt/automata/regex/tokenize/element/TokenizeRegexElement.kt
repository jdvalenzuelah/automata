package gt.automata.regex.tokenize.element

import gt.automata.regex.element.RegexElement

fun interface TokenizeRegexElement<T : RegexElement> : (String) -> T?
