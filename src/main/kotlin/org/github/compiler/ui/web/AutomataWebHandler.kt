package org.github.compiler.ui.web

import gt.webUI.AutomataWebHandler
import guru.nidi.graphviz.engine.Format
import org.github.compiler.regularExpressions.automata.Automata
import org.github.compiler.regularExpressions.automata.dfa.DeterministicFiniteAutomata
import org.github.compiler.regularExpressions.automata.dfa.simulation.DFASimulation
import org.github.compiler.regularExpressions.automata.nfa.NonDeterministicFiniteAutomata
import org.github.compiler.regularExpressions.automata.nfa.simulation.NFASimulation
import org.github.compiler.regularExpressions.graph.AutomataGraph
import java.io.ByteArrayOutputStream
import java.util.*

class AutomataWebHandler(
    private val regexToDfaDirect: (String) -> DeterministicFiniteAutomata<String, Char>,
    private val regexToDfaByNfa: (String) -> DeterministicFiniteAutomata<String, Char>,
    private val regexToNfa: (String) -> NonDeterministicFiniteAutomata<String, Char>,
    private val nfaSimulation: NFASimulation<String, Char>,
    private val dfaSimulation: DFASimulation<String, Char>,
    private val graph: AutomataGraph
) {

    data class AutomataScope(
        val dfaByNfa: DeterministicFiniteAutomata<String, Char>,
        val dfaDirect: DeterministicFiniteAutomata<String, Char>,
        val nfa: NonDeterministicFiniteAutomata<String, Char>,
        val dfaByNfaGraphBase64: String,
        val dfaDirectGraphBase64: String,
        val nfaGraphBase64: String
    )

    data class SimulationScope(
        val testString: String,
        val result: Boolean
    )

    fun getAutomataScope(regex: String): AutomataScope {
        val dfaDirect = regexToDfaDirect(regex)
        val dfaByNfa = regexToDfaByNfa(regex)
        val nfa = regexToNfa(regex)

        return AutomataScope(
            dfaByNfa,
            dfaDirect,
            nfa,
            getBase64GraphIMG(dfaByNfa),
            getBase64GraphIMG(dfaDirect),
            getBase64GraphIMG(nfa)
        )

    }

    fun getSimulationScope(nfa: NonDeterministicFiniteAutomata<String, Char>, testString: String): SimulationScope {
        return SimulationScope(testString, nfaSimulation.simulate(nfa, testString.asIterable()))
    }

    fun getSimulationScope(dfa: DeterministicFiniteAutomata<String, Char>, testString: String): SimulationScope {
        return SimulationScope(testString, dfaSimulation.simulate(dfa, testString.asIterable()))
    }

    private fun getBase64GraphIMG(automata: Automata<*, *, *>): String {
        val output = ByteArrayOutputStream()

        graph.graph(automata)
            .render(Format.PNG)
            .toOutputStream(output)

        val graphOutput = output.toByteArray()

        val b64Img = String(Base64.getEncoder().encode(graphOutput))
        return "data:image/png;base64,$b64Img"
    }

}
