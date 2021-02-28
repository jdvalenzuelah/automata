package gt.automata.nfa

import gt.automata.nfa.models.INFA
import gt.automata.regex.RegexExpression

fun interface RegexToNFA<T, U> : (RegexExpression) -> INFA<T, U>
