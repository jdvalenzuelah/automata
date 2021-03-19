package org.github.compiler.regularExpressions.automata.simulation

import org.github.compiler.regularExpressions.automata.Automata

interface AutomataSimulation<S, I, O, A: Automata<S, I, O>> {
    fun simulate(automata: A, input: Iterable<I>): Boolean
}
