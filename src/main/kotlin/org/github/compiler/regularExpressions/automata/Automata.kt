package org.github.compiler.regularExpressions.automata

interface Automata<S, I, O> : Iterable<ITransition<S, I>> {

    val states: Collection<IState<S>> // S

    val initialState: IState<S> // So

    val finalStates: Collection<IState<S>> // F

    val alphabet: Collection<I>

    fun move(state: IState<S>, char: I): O? // Transition function

}
