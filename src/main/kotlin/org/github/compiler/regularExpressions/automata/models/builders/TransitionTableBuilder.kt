package org.github.compiler.regularExpressions.automata.models.builders

import org.github.compiler.regularExpressions.automata.IState
import org.github.compiler.regularExpressions.automata.TransitionTable
import org.github.compiler.regularExpressions.automata.models.MapTransitionTable
import org.github.compiler.regularExpressions.automata.models.State

@DslMarker
annotation class TransitionTableDsl

@TransitionTableDsl
class TransitionTableBuilder<S, I> {

    private val transitions = MapTransitionTable<S, I>(mutableMapOf())

    @JvmName("simpleBy")
    infix fun Pair<S, S>.by(i: I) {
        State(first) to State(second) by i
    }

    infix fun Pair<IState<S>, IState<S>>.by(i: I) {
        transitions.add(i, first, setOf(second))
    }

    fun build(): TransitionTable<S, I> = transitions

}

inline fun <S, I> transitions(init: TransitionTableBuilder<S, I>.() -> Unit): TransitionTable<S, I> {
    return TransitionTableBuilder<S, I>().apply(init).build()
}
