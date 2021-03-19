package org.github.compiler.regularExpressions.automata.nfa.operations

import org.github.compiler.regularExpressions.automata.IState
import org.github.compiler.regularExpressions.automata.models.State
import org.github.compiler.regularExpressions.automata.models.builders.nfa
import org.github.compiler.regularExpressions.automata.models.builders.transitions
import org.github.compiler.regularExpressions.automata.nfa.NonDeterministicFiniteAutomata
import org.github.compiler.regularExpressions.automata.nfa.models.NFA
import org.tinylog.kotlin.Logger

fun interface IdGenStrategy<S> : () -> S

class ThompsonImpl<S, I>(
    private val epsilonValue: I,
    private val idGenerationStrategy: IdGenStrategy<S>
) : ThompsonConstruction<S, I> {

    override fun empty(): NonDeterministicFiniteAutomata<S, I> {
        Logger.debug("Generating nfa for epsilonValue=$epsilonValue")
        return symbol(epsilonValue)
    }

    override fun symbol(s: I): NonDeterministicFiniteAutomata<S, I> {
        Logger.debug("Generating nfa for symbol=$s")
        val from = idGenerationStrategy()
        val to = idGenerationStrategy()
        return nfa {
            states {
                state { from }
                state { to }
            }
            initialState(from)
            finalStates(to)
            transitions {
                from to to by s
            }
        }
    }

    override fun or(nfa1: NonDeterministicFiniteAutomata<S, I>, nfa2: NonDeterministicFiniteAutomata<S, I>): NonDeterministicFiniteAutomata<S, I> {
        Logger.debug("Generating nfa for nfa1=$nfa1 or nfa2=$nfa2")
        val initial = idGenerationStrategy()
        val final = idGenerationStrategy()

        val combinedStates = nfa1.states + nfa2.states
        val combinedTransitionTable = nfa1.transitionTable + nfa2.transitionTable

        return nfa {
            states {
                combinedStates.forEach { +it }
                state { initial }
                state { final }
            }
            initialState(initial)
            finalStates(final)
            transitions {
                combinedTransitionTable.forEach { transition ->
                    transition.to.forEach { toSate ->
                        transition.from to toSate by transition.edge
                    }
                }
                State(initial) to nfa1.initialState by epsilonValue
                State(initial) to nfa2.initialState by epsilonValue
                nfa1.finalStates.forEach { finalSate -> finalSate to State(final) by epsilonValue }
                nfa2.finalStates.forEach { finalSate -> finalSate to State(final) by epsilonValue }
            }
        }


    }

    override fun concat(nfa1: NonDeterministicFiniteAutomata<S, I>, nfa2: NonDeterministicFiniteAutomata<S, I>): NonDeterministicFiniteAutomata<S, I> {
        Logger.debug("Generating nfa for nfa1=$nfa1 concat nfa2=$nfa2")
        val newTransitions = nfa1.transitionTable + transitions {
            nfa2.transitionTable.forEach { transition ->
                val newFrom = if(transition.from == nfa2.initialState) nfa1.finalStates else listOf(transition.from)
                newFrom.forEach { fromState ->
                    transition.to.forEach { toState ->
                        val newToState = if(toState == nfa2.initialState) nfa1.finalStates else listOf(toState)
                        newToState.forEach {
                            fromState to it by transition.edge
                        }
                    }
                }
            }
        }

        val combinedStates = nfa1.states + nfa2.states.filter { it != nfa2.initialState }
        val combinedAlphabet = nfa1.alphabet + nfa2.alphabet
        return NFA(combinedAlphabet, nfa2.finalStates, nfa1.initialState, combinedStates, newTransitions)
    }

    override fun closure(nfa1: NonDeterministicFiniteAutomata<S, I>): NonDeterministicFiniteAutomata<S, I> {
        Logger.debug("Generating nfa for closure nfa1=$nfa1")
        val initial = idGenerationStrategy()
        val final = idGenerationStrategy()

        return nfa {
            states {
                state { initial }
                state { final }
                nfa1.states.forEach { +it }
            }

            initialState(initial)
            finalStates(final)

            transitions {
                nfa1.forEach { transition ->
                    transition.to.forEach { toState ->
                        transition.from to toState by transition.edge
                    }
                }
                State(initial) to nfa1.initialState by epsilonValue
                State(initial) to State(final) by epsilonValue
                nfa1.finalStates.forEach {
                    it to nfa1.initialState by epsilonValue
                    it to State(final) by epsilonValue
                }
            }
        }

    }

    override fun positiveClosure(nfa1: NonDeterministicFiniteAutomata<S, I>): NonDeterministicFiniteAutomata<S, I> {
        Logger.debug("Generating nfa positive closure as rr* of nfa1=$nfa1")
        val nfaClosure = closure(nfa1)

        val mappedStates = mutableMapOf<IState<S>, IState<S>>()

        val newStates = nfa1.states.map { current ->
            val new = State(idGenerationStrategy())
            mappedStates[current] = new
            new
        }

        val initialStates = mappedStates.getOrPut(nfa1.initialState) { State(idGenerationStrategy()) }
        val finalStates = nfa1.finalStates.map { current ->
            mappedStates.getOrPut(current) { State(idGenerationStrategy()) }
        }
        val newTransitions = transitions<S, I> {
            nfa1.transitionTable.forEach { transition ->
                transition.to.forEach { toState ->
                    val newToState = mappedStates.getOrPut(toState) { State(idGenerationStrategy()) }
                    val newFromState = mappedStates.getOrPut(transition.from) { State(idGenerationStrategy()) }
                    newFromState to newToState by transition.edge
                }
            }
        }

        val newNfa = NFA(nfa1.alphabet, finalStates, initialStates, newStates, newTransitions)

        return concat(newNfa, nfaClosure)
    }

    override fun zeroOrOne(nfa1: NonDeterministicFiniteAutomata<S, I>): NonDeterministicFiniteAutomata<S, I> {
        Logger.debug("Generating nfa zero or one as r|$epsilonValue of nfa1=$nfa1")
        return or(nfa1, empty())
    }

}
