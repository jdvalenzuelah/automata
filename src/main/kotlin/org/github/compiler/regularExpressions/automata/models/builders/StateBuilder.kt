package org.github.compiler.regularExpressions.automata.models.builders

import org.github.compiler.regularExpressions.automata.IState
import org.github.compiler.regularExpressions.automata.models.State

@DslMarker
annotation class StatesDsl

@StatesDsl
class StateBuilder<S> {

    private val states = mutableSetOf<IState<S>>()

    operator fun S.unaryPlus() {
        states.add(State(this))
    }

    operator fun IState<S>.unaryPlus() {
        states.add(this)
    }

    fun state(get: () -> S) {
        states.add(State(get()))
    }

    fun states(vararg st: IState<S>) {
        states.addAll(st)
    }

    fun build(): Collection<IState<S>> = states

}

inline fun <S> states( init: StateBuilder<S>.() -> Unit ) : Collection<IState<S>> {
    return StateBuilder<S>().apply(init).build()
}
