package org.github.compiler.regularExpressions.automata.configuration

import org.github.compiler.regularExpressions.automata.dfa.DeterministicFiniteAutomata
import org.github.compiler.regularExpressions.automata.dfa.simulation.DFASimulation
import org.github.compiler.regularExpressions.automata.nfa.NonDeterministicFiniteAutomata
import org.github.compiler.regularExpressions.automata.nfa.operations.EpsilonClosure
import org.github.compiler.regularExpressions.automata.nfa.operations.EpsilonClosureImpl
import org.github.compiler.regularExpressions.automata.nfa.simulation.NFASimulation
import org.github.compiler.regularExpressions.automata.simulation.AutomataSimulation
import org.github.compiler.regularExpressions.regex.elements.epsilon
import java.lang.IllegalArgumentException

object AutomataSimulationFactory {
    fun dfaSimulation(): DFASimulation<String, Char> = DFASimulation()
    fun nfaSimulation(): NFASimulation<String, Char> = NFASimulation(EpsilonClosureImpl(epsilon))
}
