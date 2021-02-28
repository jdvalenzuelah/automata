package gt.automata.nfa.models

@DslMarker
annotation class NFADsl

@NFADsl
class NFABuilder<S, I> {
    private var states = mutableListOf<State<S>>()
    private var finalStates = mutableListOf<State<S>>()
    private var initialState: State<S>? = null
    private var transitions: TransitionTable<S, I> = mutableMapOf()

    @NFADsl
    class StatesBuilder<S> {
        private var states = mutableListOf<State<S>>()

        operator fun S.unaryPlus() {
            this@StatesBuilder.states.add(State(this@unaryPlus))
        }

        fun build(): MutableList<State<S>> {
            require(states.isNotEmpty()) { "States cannot be an empty set" }
            return states
        }
    }

    @NFADsl
    class TransitionTableBuilder<S, I> {
        private val transitionTable: MutableTransitionTable<S, I> = mutableMapOf()

        infix fun Pair<S, S>.by(i: I) {
            val fromState = State(this@by.first)
            val toState = State(this@by.second)
            val currentTransitions = transitionTable[fromState]

            if(currentTransitions == null)
                transitionTable[fromState]=  mutableMapOf(i to toState)
            else
                currentTransitions[i] =  toState
        }

        fun build(): TransitionTable<S, I> = transitionTable

    }

    fun states(init: StatesBuilder<S>.() -> Unit) {
        states = StatesBuilder<S>().apply(init).build()
    }

    fun initialState(state: S) {
        initialState = State(state)
    }

    fun finalStates(vararg states: S) {
        require(states.isNotEmpty()) { "Final states cannot be empty" }
        finalStates = states.map { State(it) }.toMutableList()
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
