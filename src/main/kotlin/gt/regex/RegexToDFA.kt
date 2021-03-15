package gt.regex

import gt.automata.dfa.DeterministicFiniteAutomata

fun interface RegexToDFA <T, U> : (RegexExpression) -> DeterministicFiniteAutomata<T, U>
