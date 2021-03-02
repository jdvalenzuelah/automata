package gt.automata.nfa.thompson

import gt.automata.nfa.epsilon
import gt.automata.nfa.NonDeterministicFiniteAutomata
import gt.automata.nfa.models.*

class ThomptsonRules : ThompsonConstruction<Int, String> {

    private var idCounter: Int = 0

    private fun getId(): Int {
        idCounter++
        return idCounter
    }

    override fun empty(): NonDeterministicFiniteAutomata<Int, String> = symbol(epsilon)

    override fun symbol(s: String): NonDeterministicFiniteAutomata<Int, String> {
        val from = getId()
        val to = getId()
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

    override fun or(nfa1: NonDeterministicFiniteAutomata<Int, String>, nfa2: NonDeterministicFiniteAutomata<Int, String>): NonDeterministicFiniteAutomata<Int, String> {
        val initial = getId()
        val final = getId()

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
                combinedTransitionTable.forEach { (fromState, transitions) ->
                    transitions.forEach { (action, toStates) ->
                        toStates.forEach { toState ->
                            fromState to toState by action
                        }
                    }
                }
                State(initial) to nfa1.initialState by epsilon
                State(initial) to nfa2.initialState by epsilon
                nfa1.finalStates.forEach { finalSate -> finalSate to State(final) by epsilon }
                nfa2.finalStates.forEach { finalSate -> finalSate to State(final) by epsilon }
            }
        }


    }

    override fun concat(nfa1: NonDeterministicFiniteAutomata<Int, String>, nfa2: NonDeterministicFiniteAutomata<Int, String>): NonDeterministicFiniteAutomata<Int, String> {

        val newTransitionTable: TransitionTable<Int, String> = nfa1.transitionTable + nfa2.transitionTable.map { (fromState, transitions) ->
            val newFromState = if(fromState == nfa2.initialState) nfa1.finalStates else listOf(fromState)
            val newTransitions = transitions.map { (action, toStates) ->
                val newToStates = toStates.map { toState ->
                    if(toState == nfa2.initialState) nfa1.finalStates else listOf(toState)
                }
                action to newToStates.flatten()
            }.toMap()

            newFromState.map { it to newTransitions }
        }.flatten()
            .toMap()

        val combinedStates = nfa1.states + nfa2.states.filter { it != nfa2.initialState }

        return NFA(combinedStates, nfa1.initialState, nfa2.finalStates, newTransitionTable)
    }

    override fun closure(nfa1: NonDeterministicFiniteAutomata<Int, String>): NonDeterministicFiniteAutomata<Int, String> {
        val initial = getId()
        val final = getId()

        return nfa {
            states {
                state { initial }
                state { final }
                nfa1.states.forEach { +it }
            }

            initialState(initial)
            finalStates(final)

            transitions {
                nfa1.transitionTable.forEach { (fromState, transitions) ->
                    transitions.forEach { (action, toStates) ->
                        toStates.forEach { toState ->
                            fromState to toState by action
                        }
                    }
                }
                State(initial) to nfa1.initialState by epsilon
                State(initial) to State(final) by epsilon
                nfa1.finalStates.forEach {
                    it to nfa1.initialState by epsilon
                    it to State(final) by epsilon
                }
            }
        }

    }


}
