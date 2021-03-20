package org.github.compiler.regularExpressions.regex.tokenize.element

import org.github.compiler.regularExpressions.regex.elements.RegexElement
import org.github.compiler.regularExpressions.transforms.Transform

fun interface RegexElementTokenizer<out T: RegexElement> : Transform<Char, T?>
