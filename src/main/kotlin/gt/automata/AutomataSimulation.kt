package gt.automata

interface AutomataSimulation<S, I, O, A: Automata<S, I, O>> {
    fun simulate(automata: A, input: Iterable<I>): Boolean
}
