package org.github.compiler.regularExpressions.automata.nfa.operations

import org.github.compiler.regularExpressions.automata.IState
import org.github.compiler.regularExpressions.automata.nfa.NonDeterministicFiniteAutomata

class EpsilonClosureImpl<S, I>(
    private val epsilonValue: I
) : EpsilonClosure<S, I> {

    override fun epsilonClosure(nfa: NonDeterministicFiniteAutomata<S, I>, vararg t: IState<S>): Collection<IState<S>> {
        val eClosure = mutableListOf<IState<S>>().apply { addAll(t) }
        val stateStack = ArrayDeque<IState<S>>().apply { addAll(t) }

        while (stateStack.isNotEmpty()) {
            val currentState = stateStack.removeLast()

            nfa.move(currentState, epsilonValue)?.forEach { destState ->
                if(destState !in eClosure) {
                    eClosure.add(destState)
                    stateStack.addLast(destState)
                }
            }
        }
        return eClosure
    }

}
