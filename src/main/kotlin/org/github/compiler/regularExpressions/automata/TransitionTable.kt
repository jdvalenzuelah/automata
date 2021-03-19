package org.github.compiler.regularExpressions.automata

interface TransitionTable<S, INPUT>: Iterable<ITransition<S, INPUT>> {
    operator fun get(input: INPUT, from: IState<S>): Collection<IState<S>>?
    operator fun plus(other: TransitionTable<S, INPUT>): TransitionTable<S, INPUT>
}

interface MutableTransitionTable<S, INPUT>: TransitionTable<S, INPUT> {
    operator fun set(input: INPUT, from: IState<S>, to: Collection<IState<S>>)
    fun add(input: INPUT, from: IState<S>, to: Collection<IState<S>>)
}
