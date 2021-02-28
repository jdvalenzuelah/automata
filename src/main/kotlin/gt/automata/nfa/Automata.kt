package gt.automata.nfa

/*
 * Nondeterministic finite automata
 * - A finite set of states S
 * - ∑ input alphabet (ε stands for empty string)
 * - Transition function
 * - So initial state
 * - F set of final states (subset of A)
 */
interface Automata<S, I> {

    val states: Collection<IState<S>> // S

    val initialState: IState<S> // So

    val finalStates: Collection<IState<S>> // F

    fun move(state: IState<S>, char: I): Collection<IState<S>>? // Transition function

}
