package gt.graphing

import gt.automata.nfa.NonDeterministicFiniteAutomata
import guru.nidi.graphviz.attribute.Label
import guru.nidi.graphviz.attribute.Rank
import guru.nidi.graphviz.engine.Graphviz
import guru.nidi.graphviz.graph
import guru.nidi.graphviz.toGraphviz

class NFAGraph<T, U> {
    fun graphFromNfa(nfa: NonDeterministicFiniteAutomata<T, U>): Graphviz {
        // TODO: add double circle for final state `Shape.DOUBLE_CIRCLE`
        return graph(directed = true) {
            graph[Rank.dir(Rank.RankDir.LEFT_TO_RIGHT)]

            nfa.transitionTable.forEach { (fromState, transitions) ->
                transitions.forEach { (action, toStates) ->
                    toStates.forEach { toState ->
                        (fromState.toString() - toState.toString())[Label.of(action.toString())]
                    }
                }
            }
        }.toGraphviz()

    }
}
