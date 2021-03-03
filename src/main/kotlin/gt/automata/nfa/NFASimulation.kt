package gt.automata.nfa

import gt.automata.AutomataSimulation
import gt.automata.IState
import gt.automata.nfa.operations.EpsilonClosure

class NFASimulation<S, I>(
    private val epsilonClosure: EpsilonClosure<S, I>
) : AutomataSimulation<S, I, Collection<IState<S>>, NonDeterministicFiniteAutomata<S, I>> {

    override fun simulate(automata: NonDeterministicFiniteAutomata<S, I>, input: Iterable<I>): Boolean {
        var s = epsilonClosure.eClosure(automata, automata.initialState)

        input.forEach { char ->
            s = s.mapNotNull { state -> automata.move(state, char) }.flatten()
        }

        return s.any { it in automata.finalStates }
    }

}
