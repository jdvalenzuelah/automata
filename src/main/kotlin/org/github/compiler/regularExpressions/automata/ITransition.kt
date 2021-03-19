package org.github.compiler.regularExpressions.automata

interface ITransition<S, I> {
    val from: IState<S>
    val to: Collection<IState<S>>
    val edge: I
}
