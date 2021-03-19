package org.github.compiler.regularExpressions.automata

fun Automata<*, *, *>.toFileFormat(): String {
    val states = states.joinToString(separator = ", ") { "$it" }
    val symbols = alphabet.joinToString(separator = ", ") { "$it" }
    val acceptStates = finalStates.joinToString(separator = ", ") { "$it" }

    val transition = flatMap { trans ->
        trans.to.map {
            "(${trans.from}, ${trans.edge}, $it)"
        }
    }.joinToString(separator = "-")

    return """
        ESTADOS = {$states}
        SIMBOLOS = {$symbols}
        INICIO = {$initialState}
        ACEPTACION = {$acceptStates}
        TRANSICION = $transition
    """.trimIndent()
}
