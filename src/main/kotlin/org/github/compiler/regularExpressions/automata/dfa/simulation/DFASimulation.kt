package org.github.compiler.regularExpressions.automata.dfa.simulation

import org.github.compiler.regularExpressions.automata.IState
import org.github.compiler.regularExpressions.automata.dfa.DeterministicFiniteAutomata
import org.github.compiler.regularExpressions.automata.simulation.AutomataSimulation

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
