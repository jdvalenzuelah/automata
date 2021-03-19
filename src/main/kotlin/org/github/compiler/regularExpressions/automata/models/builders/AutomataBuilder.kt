package org.github.compiler.regularExpressions.automata.models.builders

import org.github.compiler.regularExpressions.automata.Automata
import org.github.compiler.regularExpressions.automata.IState
import org.github.compiler.regularExpressions.automata.TransitionTable
import org.github.compiler.regularExpressions.automata.dfa.DeterministicFiniteAutomata
import org.github.compiler.regularExpressions.automata.dfa.models.DFA
import org.github.compiler.regularExpressions.automata.nfa.models.NFA
import org.github.compiler.regularExpressions.automata.models.MapTransitionTable
import org.github.compiler.regularExpressions.automata.models.State
import org.github.compiler.regularExpressions.automata.nfa.NonDeterministicFiniteAutomata

@DslMarker
annotation class AutomataDsl

@AutomataDsl
class AutomataBuilder<S, I, O, A : Automata<S, I, O>> {

    private var states = mutableSetOf<IState<S>>()
    private var transition: TransitionTable<S, I> = MapTransitionTable(mutableMapOf())
    private var finalStates = mutableSetOf<IState<S>>()
    var initialState: IState<S>? = null

    fun states(init: StateBuilder<S>.() -> Unit) {
        states = StateBuilder<S>().apply(init).build().toMutableSet()
    }

    fun transitions(init: TransitionTableBuilder<S, I>.() -> Unit) {
        transition = TransitionTableBuilder<S, I>().apply(init).build()
    }

    fun initialState(s: S) {
        initialState = State(s)
    }

    fun finalStates(vararg states: S) {
        finalStates.addAll(states.map { State(it) })
    }

    fun finalStates(vararg states: IState<S>) {
        finalStates.addAll(states)
    }

    fun finalStates(states: Collection<IState<S>>) {
        finalStates.addAll(states)
    }

    fun build(
        buildStrategy: (Set<I>, Set<IState<S>>, IState<S>, Set<IState<S>>, TransitionTable<S, I>) -> A
    ): A {
        val alphabet = transition.map { it.edge }.toSet()
        return buildStrategy(alphabet, finalStates, initialState!!, states, transition)
    }
}


fun <S, I> dfa(init: AutomataBuilder<S, I, IState<S>, DeterministicFiniteAutomata<S, I>>.() -> Unit):  DeterministicFiniteAutomata<S, I> {
    return AutomataBuilder<S, I, IState<S>,DeterministicFiniteAutomata<S, I>>()
        .apply(init)
        .build { alphabet, finalStates, initialState, states, transition ->
            DFA(alphabet, finalStates, initialState, states, transition)
        }
}


fun <S, I> nfa(init: AutomataBuilder<S, I, Collection<IState<S>>, NonDeterministicFiniteAutomata<S, I>>.() -> Unit):  NonDeterministicFiniteAutomata<S, I> {
    return AutomataBuilder<S, I, Collection<IState<S>>, NonDeterministicFiniteAutomata<S, I>>()
        .apply(init)
        .build { alphabet, finalStates, initialState, states, transition ->
            NFA(alphabet, finalStates, initialState, states, transition)
        }
}
