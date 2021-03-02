package gt.automata.nfa

import gt.automata.nfa.models.NonDeterministicFiniteAutomata
import gt.automata.regex.RegexExpression

fun interface RegexToNFA<T, U> : (RegexExpression) -> NonDeterministicFiniteAutomata<T, U>
