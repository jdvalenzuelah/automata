package org.github.compiler.regularExpressions.automata.dfa.models

import org.github.compiler.regularExpressions.automata.IState
import org.github.compiler.regularExpressions.automata.ITransition
import org.github.compiler.regularExpressions.automata.TransitionTable
import org.github.compiler.regularExpressions.automata.dfa.DeterministicFiniteAutomata

data class DFA<S, I>(
    override val alphabet: Collection<I>,
    override val finalStates: Collection<IState<S>>,
    override val initialState: IState<S>,
    override val states: Collection<IState<S>>,
    override val transitionTable: TransitionTable<S, I>
) : DeterministicFiniteAutomata<S, I> {

    init {
        require(initialState in states) { "Initial state must be part of states" }
        require(states.containsAll(finalStates)) { "Final states must be a subset of all states" }
    }

    override fun move(state: IState<S>, char: I): IState<S>? {
        return transitionTable[char, state]?.firstOrNull()
    }

    override fun iterator(): Iterator<ITransition<S, I>> {
        return transitionTable.iterator()
    }

}
