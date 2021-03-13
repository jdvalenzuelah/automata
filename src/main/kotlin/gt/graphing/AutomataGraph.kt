package gt.graphing

import gt.automata.IState
import gt.automata.dfa.DeterministicFiniteAutomata
import gt.automata.nfa.NonDeterministicFiniteAutomata
import guru.nidi.graphviz.KraphvizContext
import guru.nidi.graphviz.attribute.*
import guru.nidi.graphviz.engine.Graphviz
import guru.nidi.graphviz.graph
import guru.nidi.graphviz.toGraphviz



// TODO: Refactor transition table to avoid so many loops
class AutomataGraph<T, U> {

    private data class Transitions<S, L>(val from: S, val to: S, val by: L)

    private fun baseGraph(config: KraphvizContext.() -> Unit) = graph(directed = true) {
        graph[Rank.dir(Rank.RankDir.LEFT_TO_RIGHT)]
        "fake"[Style.INVIS]
        config()
    }

    fun graphFromNfa(nfa: NonDeterministicFiniteAutomata<T, U>): Graphviz {
        return baseGraph {
            nfa.states.forEach {
                when (it) {
                    in nfa.finalStates -> it.toString()[Shape.DOUBLE_CIRCLE]
                    else -> it.toString()
                }
            }

            "fake" - nfa.initialState.toString()
            nfa.transitionTable.flatMap { (fromState, transitions) ->
                transitions.flatMap { (action, toStates) ->
                    toStates.map { toState ->
                        (fromState.toString() - toState.toString())[Label.of(action.toString())]
                    }
                }
            }
        }.toGraphviz()
    }

    fun graphFromDfa(dfa: DeterministicFiniteAutomata<T, U>): Graphviz {
        return baseGraph {
            dfa.states.forEach {
                when (it) {
                    in dfa.finalStates -> it.toString()[Shape.DOUBLE_CIRCLE]
                    else -> it.toString()
                }
            }

            "fake" - dfa.initialState.toString()
            dfa.transitionTable.flatMap { (fromState, transitions) ->
                transitions.map { (action, toState) ->
                    (fromState.toString() - toState.toString())[Label.of(action.toString())]
                }
            }
        }.toGraphviz()
    }
}
