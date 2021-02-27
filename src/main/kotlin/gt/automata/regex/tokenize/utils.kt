package gt.automata.regex.tokenize

import gt.automata.regex.element.Character
import gt.automata.regex.element.Grouping
import gt.automata.regex.element.Operator
import gt.automata.regex.element.RegexElement


fun RegexElement?.isOperator() = this != null && this is Operator

fun RegexElement?.isGrouping() = this != null && this is Grouping

fun RegexElement?.isCharacter() = this != null && this is Character
