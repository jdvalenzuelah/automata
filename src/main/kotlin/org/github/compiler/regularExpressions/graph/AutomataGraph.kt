package org.github.compiler.regularExpressions.graph

import guru.nidi.graphviz.KraphvizContext
import guru.nidi.graphviz.attribute.Label
import guru.nidi.graphviz.attribute.Rank
import guru.nidi.graphviz.attribute.Shape
import guru.nidi.graphviz.attribute.Style
import guru.nidi.graphviz.engine.Graphviz
import guru.nidi.graphviz.model.MutableNode
import guru.nidi.graphviz.toGraphviz
import guru.nidi.graphviz.graph as graphviz
import org.github.compiler.regularExpressions.automata.Automata
import org.github.compiler.regularExpressions.automata.IState
import org.github.compiler.regularExpressions.automata.dfa.DeterministicFiniteAutomata

object AutomataGraph {

    private fun Automata<*,*,*>.isFinal(state: IState<*>): Boolean = state in finalStates

    private fun KraphvizContext.getStateNode(
        automata: Automata<*, *, *>,
        state: IState<*>
    ): MutableNode {
        val shape = if(automata.isFinal(state)) Shape.DOUBLE_CIRCLE else Shape.CIRCLE
        return "$state"[shape]
    }

    fun graph(automata: Automata<*, *, *>): Graphviz {
        return graphviz(directed = true) {
            graph[Rank.dir(Rank.RankDir.LEFT_TO_RIGHT)]

            "fake"[Style.INVIS] - "${automata.initialState}"

            automata.forEach { transition ->
                transition.to.forEach { toState ->
                    (getStateNode(automata, transition.from) - getStateNode(automata, toState))[Label.of("${transition.edge}")]
                }
            }

        }.toGraphviz()
    }

}
