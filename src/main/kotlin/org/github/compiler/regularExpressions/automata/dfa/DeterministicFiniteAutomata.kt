package org.github.compiler.regularExpressions.automata.dfa

import org.github.compiler.regularExpressions.automata.Automata
import org.github.compiler.regularExpressions.automata.IState
import org.github.compiler.regularExpressions.automata.TransitionTable


interface DeterministicFiniteAutomata<S, I> : Automata<S, I, IState<S>> {
    val transitionTable: TransitionTable<S, I>
}
