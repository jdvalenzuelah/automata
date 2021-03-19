package org.github.compiler.regularExpressions.automata.nfa.operations

import org.github.compiler.regularExpressions.automata.dfa.DeterministicFiniteAutomata
import org.github.compiler.regularExpressions.automata.nfa.NonDeterministicFiniteAutomata

fun interface SubSetConstruction<S, I, SS, II> : (NonDeterministicFiniteAutomata<S, I>) -> DeterministicFiniteAutomata<SS, II>
