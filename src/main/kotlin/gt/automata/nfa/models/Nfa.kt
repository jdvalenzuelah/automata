package gt.automata.nfa.models

import gt.automata.nfa.IState
import gt.automata.nfa.INFA

data class NFA<S, I>(
    override val states: Collection<IState<S>>,
    override val initialState: IState<S>,
    override val finalStates: Collection<IState<S>>,
    val transitionTable: TransitionTable<S, I>
): INFA<S, I> {

    init {
        require(initialState in states) { "Initial state must be part of final state" }
        require(states.containsAll(finalStates)) { "Final states must be a subset of all states" }
    }

    override fun move(state: IState<S>, char: I): IState<S>? {
        require(state in states) { "Input state must be part of states" }

        val transitions = transitionTable[state]

        //TODO: throw exception if null?

        if(transitions.isNullOrEmpty())
            return null

        return transitions[char]

    }

}

