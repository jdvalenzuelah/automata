package gt.regex.tokenize

import gt.regex.element.Character
import gt.regex.element.Grouping
import gt.regex.element.Operator
import gt.regex.element.RegexElement


fun RegexElement?.isOperator() = this != null && this is Operator

fun RegexElement?.isGrouping() = this != null && this is Grouping

fun RegexElement?.isCharacter() = this != null && this is Character
