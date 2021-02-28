package gt.automata.nfa.models

import gt.automata.nfa.IState

@DslMarker
annotation class NFADsl

@NFADsl
class NFABuilder<S, I> {
    private var states = mutableListOf<IState<S>>()
    private var finalStates = mutableListOf<IState<S>>()
    private var initialState: IState<S>? = null
    private var transitions: TransitionTable<S, I> = mutableMapOf()

    @NFADsl
    class StatesBuilder<S> {
        private var states = mutableListOf<IState<S>>()

        operator fun S.unaryPlus() {
            this@StatesBuilder.states.add(State(this@unaryPlus))
        }

        operator fun IState<S>.unaryPlus() {
            this@StatesBuilder.states.add(this@unaryPlus)
        }

        fun state(init: () -> S) {
            +init()
        }

        fun build(): MutableList<IState<S>> {
            require(states.isNotEmpty()) { "States cannot be an empty set" }
            return states
        }
    }

    @NFADsl
    class TransitionTableBuilder<S, I> {
        private val transitionTable: MutableTransitionTable<S, I> = mutableMapOf()

        @JvmName("simpleBy")
        infix fun Pair<S, S>.by(i: I) {
            State(this@by.first) to State(this@by.second) by i
        }

        infix fun Pair<IState<S>, IState<S>>.by(i: I) {
            val fromState = this@by.first
            val toState = this@by.second
            val currentTransitions = transitionTable[fromState]

            if(currentTransitions == null)
                transitionTable[fromState] = mutableMapOf(i to mutableListOf(toState))
            else
                currentTransitions[i]?.add(toState)
        }

        fun build(): TransitionTable<S, I> = transitionTable

    }

    fun states(init: StatesBuilder<S>.() -> Unit) {
        states = StatesBuilder<S>().apply(init).build()
    }

    @JvmName("simpleState")
    fun initialState(state: S) {
        initialState(State(state))
    }

    fun initialState(state: IState<S>) {
        initialState = state
    }

    @JvmName("simpleFinalStates")
    fun finalStates(vararg states: S) {
        require(states.isNotEmpty()) { "Final states cannot be empty" }
        finalStates(*states.map { State(it) }.toTypedArray())
    }

    fun finalStates(vararg states: IState<S>) {
        require(states.isNotEmpty()) { "Final states cannot be empty" }
        finalStates = states.toMutableList()
    }

    fun transitions(init: TransitionTableBuilder<S, I>.() -> Unit) {
        transitions = TransitionTableBuilder<S, I>().apply(init).build()
    }

    internal fun build(): NFA<S, I> {
        require(!states.isNullOrEmpty()) { "States are required" }
        require(initialState != null) { "initial state is required" }
        require(initialState in states) { "initial state must be part of states" }
        require(finalStates.isNotEmpty()) { "Final states are required" }

        return NFA(states, initialState!!, finalStates, transitions)
    }

}


fun <S, I> nfa(init: NFABuilder<S, I>.() -> Unit) = NFABuilder<S, I>().apply(init).build()
