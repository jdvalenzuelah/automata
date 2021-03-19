package org.github.compiler.regularExpressions.automata.nfa.operations

import org.github.compiler.regularExpressions.automata.IState
import org.github.compiler.regularExpressions.automata.nfa.NonDeterministicFiniteAutomata

interface EpsilonClosure<S, I> {
    fun epsilonClosure(nfa: NonDeterministicFiniteAutomata<S, I>, vararg t: IState<S>): Collection<IState<S>>
}
