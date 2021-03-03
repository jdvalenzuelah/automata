package gt.regex.tokenize.element

import gt.regex.element.RegexElement

fun interface TokenizeRegexElement<T : RegexElement> : (String) -> T?
