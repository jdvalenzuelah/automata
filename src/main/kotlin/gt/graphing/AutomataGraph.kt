package gt.graphing

import gt.automata.IState
import gt.automata.dfa.DeterministicFiniteAutomata
import gt.automata.nfa.NonDeterministicFiniteAutomata
import guru.nidi.graphviz.attribute.Label
import guru.nidi.graphviz.attribute.Rank
import guru.nidi.graphviz.engine.Graphviz
import guru.nidi.graphviz.graph
import guru.nidi.graphviz.toGraphviz

// TODO: Refactor transition table to avoid so many loops
class AutomataGraph<T, U> {

    private data class Transitions<S, L>(val from: S, val to: S, val by: L)


    private fun <S, L> getTransitionsFromNFA(nfa: NonDeterministicFiniteAutomata<S, L>): List<Transitions<IState<S>, L>> {
        return nfa.transitionTable.flatMap { (fromState, transitions) ->
            transitions.flatMap { (action, toStates) ->
                toStates.map { toState ->
                    Transitions(fromState, toState, action)
                }
            }
        }
    }

    private fun <S, L> getTransitionsFromDFA(dfa: DeterministicFiniteAutomata<S, L>): List<Transitions<IState<S>, L>> {
        return dfa.transitionTable.flatMap { (fromState, transitions) ->
            transitions.map { (action, toState) ->
                Transitions(fromState, toState, action)
            }
        }
    }

    private fun <S, L> graph(transitions: List<Transitions<S, L>>): Graphviz {
        // TODO: add double circle for final state `Shape.DOUBLE_CIRCLE`
        return graph(directed = true) {
            graph[Rank.dir(Rank.RankDir.LEFT_TO_RIGHT)]
            transitions.forEach {
                (it.from.toString() - it.to.toString())[Label.of(it.by.toString())]
            }
        }.toGraphviz()
    }

    fun graphFromNfa(nfa: NonDeterministicFiniteAutomata<T, U>): Graphviz {
        val transitions = getTransitionsFromNFA(nfa)
        return graph(transitions)
    }

    fun graphFromDfa(dfa: DeterministicFiniteAutomata<T, U>): Graphviz {
        val transitions = getTransitionsFromDFA(dfa)
        return graph(transitions)
    }
}
