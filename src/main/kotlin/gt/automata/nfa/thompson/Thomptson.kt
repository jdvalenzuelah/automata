package gt.automata.nfa.thompson

import gt.automata.nfa.Automata
import gt.automata.nfa.IState
import gt.automata.nfa.epsilon
import gt.automata.nfa.models.*

class Thomptson : ThompsonConstruction<Int, String> {

    private var idCounter: Int = 0

    private fun getId(): Int {
        idCounter++
        return idCounter
    }

    override fun empty(): INFA<Int, String> = symbol(epsilon)

    override fun symbol(s: String): INFA<Int, String> {
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

    override fun or(nfa1: INFA<Int, String>, nfa2: INFA<Int, String>): INFA<Int, String> {
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

    override fun concat(nfa1: INFA<Int, String>, nfa2: INFA<Int, String>): INFA<Int, String> {
        TODO()
    }

    override fun closure(nfa1: INFA<Int, String>): INFA<Int, String> {
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
