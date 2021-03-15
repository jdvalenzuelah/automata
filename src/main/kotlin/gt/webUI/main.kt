package gt.webUI

import gt.automata.dfa.DFASimulation
import gt.automata.dfa.operations.SyntaxTreeToDfa
import gt.automata.models.State
import gt.automata.nfa.NFASimulation
import gt.automata.nfa.epsilon
import gt.automata.nfa.operations.EpsilonClosure
import gt.automata.nfa.operations.SubSetConstruction
import gt.automata.nfa.thompson.configuration.ThomptsonTransformConfig
import gt.graphing.AutomataGraph
import gt.main.AutomatasHandler
import gt.regex.AugmentRegex
import gt.regex.postfix.RegexToPostfix
import gt.regex.tokenize.configuration.TokenizerConfig
import gt.regex.tree.RegexToTree
import kweb.util.random

fun main() {

    val eClosure = EpsilonClosure<Int, String>(epsilon)

    val automataHandler = AutomatasHandler(
        TokenizerConfig.expressionTokenizer(),
        ThomptsonTransformConfig.getThompsonTransform(),
        SubSetConstruction(eClosure) { State(random.nextInt(50)) },
        DFASimulation(),
        NFASimulation(eClosure),
        AutomataGraph(),
        AugmentRegex,
        RegexToPostfix,
        RegexToTree,
        SyntaxTreeToDfa
    )

    val webHandler = AutomataWebHandler(automataHandler)

    AutomataApp(webHandler)
}
