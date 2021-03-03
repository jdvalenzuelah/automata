package gt.regex

import gt.automata.nfa.NonDeterministicFiniteAutomata

fun interface RegexToNFA<T, U> : (RegexExpression) -> NonDeterministicFiniteAutomata<T, U>
