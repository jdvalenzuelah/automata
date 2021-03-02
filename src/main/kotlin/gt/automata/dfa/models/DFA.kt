package gt.automata.dfa.models

import gt.automata.IState
import gt.automata.dfa.DeterministicFiniteAutomata

data class DFA<S, I>(
    override val states: Collection<IState<S>>,
    override val initialState: IState<S>,
    override val finalStates: Collection<IState<S>>,
    override val transitionTable: TransitionTable<S, I>
) : DeterministicFiniteAutomata<S, I> {

    init {
        require(initialState in states) { "Initial state must be part of final state" }
        require(states.containsAll(finalStates)) { "Final states must be a subset of all states" }
    }

    override fun move(state: IState<S>, char: I): IState<S>? {
        return transitionTable[state]?.get(char)
    }

}
