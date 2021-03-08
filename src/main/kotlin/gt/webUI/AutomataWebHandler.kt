package gt.webUI

import gt.automata.dfa.DeterministicFiniteAutomata
import gt.automata.nfa.NonDeterministicFiniteAutomata
import gt.main.AutomatasHandler
import java.util.*

class AutomataWebHandler(
    private val automatasHandler: AutomatasHandler<Int, String>
) {


    data class AutomataScope(
        val regex: String,
        val nfa: NonDeterministicFiniteAutomata<Int, String>,
        val dfa: DeterministicFiniteAutomata<Int, String>,
        val nfaGraphBase64: String,
        val dfaGraphBase64: String
    )

    data class SimulationScope(
        val testString: String,
        val nfaRes: Boolean,
        val dfaRes: Boolean
    )

    fun getAutomataScopeFromRegex(regex: String): AutomataScope {
        val nfa = automatasHandler.getRegexNfa(regex)
        val dfa = automatasHandler.getDfaFromNfa(nfa)
        return AutomataScope(regex, nfa, dfa, getNFABase64GraphIMG(nfa), getDFABase64GraphIMG(dfa))
    }

    fun getSimulationScope(testString: Iterable<String>, nfa: NonDeterministicFiniteAutomata<Int, String>, dfa: DeterministicFiniteAutomata<Int, String>): SimulationScope {
        return SimulationScope(
            testString.joinToString(separator = ""),
            automatasHandler.getNfaSimulationResult(nfa, testString),
            automatasHandler.getDfaSimulationResult(dfa, testString)
        )
    }

    fun getSimulationNFA(testString: Iterable<String>, nfa: NonDeterministicFiniteAutomata<Int, String>): Boolean {
        return automatasHandler.getNfaSimulationResult(nfa, testString)
    }

    fun getSimulationDFA(testString: Iterable<String>, dfa: DeterministicFiniteAutomata<Int, String>): Boolean {
        return automatasHandler.getDfaSimulationResult(dfa, testString)
    }

    private fun getNFABase64GraphIMG(nfa: NonDeterministicFiniteAutomata<Int, String>): String {
        val graphOutput = automatasHandler.getNfaGraph(nfa).toByteArray()
        val b64Img = String(Base64.getEncoder().encode(graphOutput))
        return "data:image/png;base64,$b64Img"
    }

    private fun getDFABase64GraphIMG(dfa: DeterministicFiniteAutomata<Int, String>): String {
        val graphOutput = automatasHandler.getDfaGraph(dfa).toByteArray()
        val b64Img = String(Base64.getEncoder().encode(graphOutput))
        return "data:image/png;base64,$b64Img"
    }

}
