package gt.main

import gt.automata.Automata
import gt.automata.dfa.DeterministicFiniteAutomata
import gt.automata.nfa.NonDeterministicFiniteAutomata

private fun Automata<*, *, *>.toFileFormat(transitions: () -> String): String {
    val states = states.joinToString(separator = ", ") { "$it" }
    val symbols = alphabet.joinToString(separator = ", ") { "$it" }
    val acceptStates = finalStates.joinToString(separator = ", ") { "$it" }


    return """
        ESTADOS = {$states}
        SIMBOLOS = {$symbols}
        INICIO = {$initialState}
        ACEPTACION = {$acceptStates}
        TRANSICION = ${transitions()}
    """.trimIndent()
}


fun NonDeterministicFiniteAutomata<*, *>.toFileFormat(): String {
    return toFileFormat {
        transitionTable.flatMap { (from, transitions) -> transitions.map { (to, by) -> "($from, $by, $to)" } }
            .joinToString(separator = "-")
    }
}


fun DeterministicFiniteAutomata<*, *>.toFileFormat(): String {
    return toFileFormat {
        transitionTable.flatMap { (from, transitions) -> transitions.map { (to, by) -> "($from, $by, $to)" } }
            .joinToString(separator = "-")
    }
}
