package gt.automata.dfa.models

import gt.automata.IState
import gt.automata.models.State

@DslMarker
annotation class DFADsl

@DFADsl
class DFABuilder<S, I> {
    private var states = mutableListOf<IState<S>>()
    private var initialState: IState<S>? = null
    private var finalStates = mutableListOf<IState<S>>()
    private var transitions: MutableTransitionTable<S, I> = mutableMapOf()

    class StatesBuilder<S> {
        private val states = mutableListOf<IState<S>>()

        fun state(s: IState<S>) {
            this@StatesBuilder.states.add(s)
        }

        operator fun S.unaryPlus() {
            state(State(this@unaryPlus))
        }

        fun state(init: () -> S) {
            state(State(init()))
        }

        fun build() = states
    }

    class TransitionsBuilder<S, I> {
        private val transitionTable: MutableTransitionTable<S, I> = mutableMapOf()

        infix fun Pair<IState<S>, IState<S>>.by(i: I) {
            val current = transitionTable[this@by.first]

            if(current != null)
                current.put(i, this@by.second)
            else
                transitionTable[this@by.first] = mutableMapOf(i to this@by.second)
        }

        @JvmName("simpleBy")
        infix fun Pair<S, S>.by(i: I) {
            State(this@by.first) to State(this@by.second) by i
        }

        fun build() = transitionTable

    }

    fun states(init: StatesBuilder<S>.() -> Unit) {
        states = StatesBuilder<S>().apply(init).build()
    }

    fun initialState(s: IState<S>) {
        this@DFABuilder.initialState = s
    }

    fun initialState(s: S) {
        initialState(State(s))
    }

    fun finalStates(vararg states: IState<S>) {
        this@DFABuilder.finalStates = states.toMutableList()
    }

    fun finalStates(vararg states: S) {
        this@DFABuilder.finalStates = states.map { State(it) }.toMutableList()
    }

    fun transitions(init: TransitionsBuilder<S, I>.() -> Unit) {
        this@DFABuilder.transitions = TransitionsBuilder<S,I>().apply(init).build()
    }

    fun build(): DFA<S, I> {
        require(states.isNotEmpty()) { "States are required" }
        require(initialState != null) { "initial state is required" }
        require(finalStates.isNotEmpty()) { "At least one final state is required" }
        return DFA(states, initialState!!, finalStates, transitions)
    }
}


fun <S, I> dfa(init: DFABuilder<S, I>.() -> Unit): DFA<S, I> = DFABuilder<S, I>().apply(init).build()
