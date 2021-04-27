package org.github.compiler.regularExpressions.regexImpl

import org.github.compiler.regularExpressions.Regex
import org.github.compiler.regularExpressions.automata.Automata
import org.github.compiler.regularExpressions.automata.configuration.AutomataSimulationFactory
import org.github.compiler.regularExpressions.automata.simulation.AutomataSimulation
import org.github.compiler.regularExpressions.transforms.regex.ParseRegexToAutomataFactory

class RegexImpl<S, O, A: Automata<S, Char, O>>(
    private val regexAutomata: A,
    private val simulator: AutomataSimulation<S, Char, O, A>
) : Regex {

    companion object {
        operator fun invoke(pattern: String): RegexImpl<*, *, *> {
            val regex = ParseRegexToAutomataFactory.dfaParser()
                .fromNFA()
                .invoke(pattern)

            val simulation = AutomataSimulationFactory.dfaSimulation()

            return RegexImpl(regex, simulation)
        }
    }

    override fun matches(str: CharSequence): Boolean = simulator.simulate(regexAutomata, str.asIterable())
}
