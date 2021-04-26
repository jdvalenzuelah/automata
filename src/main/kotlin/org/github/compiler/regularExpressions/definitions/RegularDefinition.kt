package org.github.compiler.regularExpressions.definitions

import org.github.compiler.regularExpressions.automata.TransitionTable
import org.github.compiler.regularExpressions.automata.models.MapTransitionTable
import org.github.compiler.regularExpressions.automata.models.State
import org.github.compiler.regularExpressions.automata.models.builders.nfa
import org.github.compiler.regularExpressions.automata.nfa.NonDeterministicFiniteAutomata
import org.github.compiler.regularExpressions.automata.nfa.operations.IdGenStrategy
import org.github.compiler.regularExpressions.transforms.Transform

interface RegularDefinition<S, I> : Transform<Collection<NonDeterministicFiniteAutomata<S, I>>, NonDeterministicFiniteAutomata<S, I>>

class GenRegularDefinition<S, I>(
    private val epsilonValue: I,
    private val stateMapper: IdGenStrategy<S>
) : RegularDefinition<S, I> {

    override fun invoke(p1: Collection<NonDeterministicFiniteAutomata<S, I>>): NonDeterministicFiniteAutomata<S, I> {
        return define(*p1.toTypedArray())
    }

    private fun define(vararg automatas: NonDeterministicFiniteAutomata<S, I>): NonDeterministicFiniteAutomata<S, I> {
        val newInitialState = stateMapper()

        val combinedStates = automatas.flatMap { it.states }.distinct()
        val combinedFinalStates = automatas.flatMap { it.finalStates }

        val tempTrans: TransitionTable<S, I> = MapTransitionTable(mutableMapOf())
        val combinedTransitions = automatas.fold(tempTrans) { acc, dfa ->
            acc + dfa.transitionTable
        }


        return nfa {
            states {
                combinedStates.forEach { +it }
                state { newInitialState }
            }
            initialState(newInitialState)
            finalStates(combinedFinalStates)
            transitions {
                combinedTransitions.forEach { transition ->
                    transition.to.forEach { toSate ->
                        transition.from to toSate by transition.edge
                    }
                }

                automatas.forEach { automata ->
                    State(newInitialState) to automata.initialState by epsilonValue
                }
            }
        }

    }

}
