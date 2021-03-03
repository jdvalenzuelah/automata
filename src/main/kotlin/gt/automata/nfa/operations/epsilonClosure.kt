package gt.automata.nfa.operations

import gt.automata.IState
import gt.automata.nfa.NonDeterministicFiniteAutomata

class EpsilonClosure<S, I>(
    private val epsilonValue: I
) {
    fun eClosure(dfa: NonDeterministicFiniteAutomata<S, I>, vararg t: IState<S>): Collection<IState<S>> {
        val eClosure = mutableListOf<IState<S>>().apply { addAll(t) }
        val stateStack = ArrayDeque<IState<S>>().apply { addAll(t) }

        while (stateStack.isNotEmpty()) {
            val currentState = stateStack.removeLast()

            dfa.move(currentState, epsilonValue)?.forEach { destState ->
                if(destState !in eClosure) eClosure.add(destState)
                stateStack.addLast(destState)
            }
        }

        return eClosure
    }
}
