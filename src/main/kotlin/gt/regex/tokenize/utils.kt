package gt.regex.tokenize

import gt.regex.element.*


fun RegexElement?.isOperator() = this != null && this is Operator

fun RegexElement?.isGrouping() = this != null && this is Grouping

fun RegexElement?.isCharacter() = this != null && (this is Character || this is Augmented)
