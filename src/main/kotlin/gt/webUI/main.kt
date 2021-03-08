package gt.webUI

import gt.automata.dfa.DFASimulation
import gt.automata.models.State
import gt.automata.nfa.NFASimulation
import gt.automata.nfa.epsilon
import gt.automata.nfa.operations.EpsilonClosure
import gt.automata.nfa.operations.SubSetConstruction
import gt.automata.nfa.thompson.configuration.ThomptsonTransformConfig
import gt.graphing.AutomataGraph
import gt.main.AutomatasHandler
import gt.regex.tokenize.configuration.TokenizerConfig
import kweb.util.random

fun main() {

    val eClosure = EpsilonClosure<Int, String>(epsilon)

    val automataHandler = AutomatasHandler(
        TokenizerConfig.expressionTokenizer(),
        ThomptsonTransformConfig.getThompsonTransform(),
        SubSetConstruction(eClosure) { State(random.nextInt(50)) },
        DFASimulation(),
        NFASimulation(eClosure),
        AutomataGraph()
    )

    val webHandler = AutomataWebHandler(automataHandler)

    AutomataApp(webHandler)
}
