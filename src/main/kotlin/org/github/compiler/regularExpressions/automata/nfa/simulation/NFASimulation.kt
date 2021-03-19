package org.github.compiler.regularExpressions.automata.nfa.simulation

import org.github.compiler.regularExpressions.automata.IState
import org.github.compiler.regularExpressions.automata.nfa.NonDeterministicFiniteAutomata
import org.github.compiler.regularExpressions.automata.nfa.operations.EpsilonClosure
import org.github.compiler.regularExpressions.automata.simulation.AutomataSimulation

class NFASimulation<S, I>(
    private val epsilonClosure: EpsilonClosure<S, I>
) : AutomataSimulation<S, I, Collection<IState<S>>, NonDeterministicFiniteAutomata<S, I>> {

    override fun simulate(automata: NonDeterministicFiniteAutomata<S, I>, input: Iterable<I>): Boolean {
        var s = epsilonClosure.epsilonClosure(automata, automata.initialState)

        input.forEach { char ->
            s = s.flatMap { state ->
                val moves = automata.move(state, char) ?: emptyList()
                epsilonClosure.epsilonClosure(automata, *moves.toTypedArray())
            }
        }
        return s.any { it in automata.finalStates }
    }

}
