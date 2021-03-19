package org.github.compiler.regularExpressions.automata.nfa.models

import org.github.compiler.regularExpressions.automata.IState
import org.github.compiler.regularExpressions.automata.ITransition
import org.github.compiler.regularExpressions.automata.TransitionTable
import org.github.compiler.regularExpressions.automata.nfa.NonDeterministicFiniteAutomata

data class NFA<S, I>(
    override val alphabet: Collection<I>,
    override val finalStates: Collection<IState<S>>,
    override val initialState: IState<S>,
    override val states: Collection<IState<S>>,
    override val transitionTable: TransitionTable<S, I>,
) : NonDeterministicFiniteAutomata<S, I> {

    init {
        require(initialState in states) { "Initial state must be part of final state" }
        require(states.containsAll(finalStates)) { "Final states must be a subset of all states" }
    }

    override fun move(state: IState<S>, char: I): Collection<IState<S>> {
        require(state in states) { "Input state must be part of states" }

        return transitionTable[char, state] ?: emptySet()
    }

    override fun iterator(): Iterator<ITransition<S, I>> {
        return transitionTable.iterator()
    }

}
