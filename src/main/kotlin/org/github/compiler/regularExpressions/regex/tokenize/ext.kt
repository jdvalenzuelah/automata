package org.github.compiler.regularExpressions.regex.tokenize

import org.github.compiler.regularExpressions.regex.elements.*

fun RegexElement?.isOperator() = this != null && this is Operator

fun RegexElement?.isGrouping() = this != null && this is Grouping

fun RegexElement?.isCharacter() = this != null && (this is Character || this is Augmented)
