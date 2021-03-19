package org.github.compiler.regularExpressions.regex.tokenize.element

import org.github.compiler.regularExpressions.regex.elements.RegexElement

fun interface RegexElementTokenizer<out T: RegexElement> : (Char) -> T?
