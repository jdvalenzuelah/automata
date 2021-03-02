package gt.automata.dfa

import gt.automata.AutomataSimulation
import gt.automata.IState

class DFASimulation<S, I> : AutomataSimulation<S, I, IState<S>, DeterministicFiniteAutomata<S, I>> {

    override fun simulate(automata: DeterministicFiniteAutomata<S, I>, input: Iterable<I>): Boolean {
        var state: IState<S>? = automata.initialState
        for (char in input) {
            if(state == null)
                return false
            state = automata.move(state, char)
        }
        return state in automata.finalStates
    }

}
