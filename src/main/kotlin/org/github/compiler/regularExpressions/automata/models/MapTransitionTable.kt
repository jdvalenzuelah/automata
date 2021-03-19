package org.github.compiler.regularExpressions.automata.models

import org.github.compiler.regularExpressions.automata.IState
import org.github.compiler.regularExpressions.automata.ITransition
import org.github.compiler.regularExpressions.automata.MutableTransitionTable
import org.github.compiler.regularExpressions.automata.TransitionTable

data class MapTransitionTable<S, I>(
    val transitions: MutableMap<Pair<I, IState<S>>, MutableSet<IState<S>>>
) : MutableTransitionTable<S, I> {

    override operator fun get(input: I, from: IState<S>): Collection<IState<S>>? {
        return transitions[input to from]
    }

    override operator fun set(input: I, from: IState<S>, to: Collection<IState<S>>) {
        transitions[input to from] = to.toMutableSet()
    }

    override fun add(input: I, from: IState<S>, to: Collection<IState<S>>) {
        val existing = transitions[input to from]

        if(existing == null)
            transitions[input to from] = to.toMutableSet()
        else
            existing.addAll(to)

    }

    override fun iterator(): Iterator<ITransition<S, I>> {
        return transitions
            .asSequence()
            .map { (from, to) -> Transition(from.first, from.second, to) }
            .iterator()
    }

    override fun plus(other: TransitionTable<S, I>): TransitionTable<S, I> {
        val otherTransitions = other
            .groupBy { it.edge to it.from }
            .map { it.key to it.value.flatMap { tran -> tran.to }.toMutableSet() }
            .toMap()

        val combinedTransitions = this.transitions + otherTransitions

        return MapTransitionTable(combinedTransitions.toMutableMap())
    }

}
