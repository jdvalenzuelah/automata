package org.github.compiler.regularExpressions.automata.nfa

import org.github.compiler.regularExpressions.automata.Automata
import org.github.compiler.regularExpressions.automata.IState
import org.github.compiler.regularExpressions.automata.TransitionTable


interface NonDeterministicFiniteAutomata<S, I> : Automata<S, I, Collection<IState<S>>> {
    val transitionTable: TransitionTable<S, I>
}
