package gt.graphing

import gt.automata.nfa.models.INFA
import guru.nidi.graphviz.attribute.Label
import guru.nidi.graphviz.attribute.Rank
import guru.nidi.graphviz.engine.Graphviz
import guru.nidi.graphviz.graph
import guru.nidi.graphviz.toGraphviz

class NFAGraph<T, U> {
    fun graphFromNfa(nfa: INFA<T, U>): Graphviz {

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
