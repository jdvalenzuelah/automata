package gt.automata.nfa

import gt.regex.RegexExpression

fun interface RegexToNFA<T, U> : (RegexExpression) -> NonDeterministicFiniteAutomata<T, U>
