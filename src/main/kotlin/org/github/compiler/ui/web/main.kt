package org.github.compiler.ui.web

import org.github.compiler.regularExpressions.automata.configuration.AutomataSimulationFactory
import org.github.compiler.regularExpressions.graph.AutomataGraph
import org.github.compiler.regularExpressions.transforms.regex.ParseRegexToAutomataFactory

fun main() {

    val dfaParserFactory = ParseRegexToAutomataFactory.dfaParser()

    val webHandler = AutomataWebHandler(
        dfaParserFactory.fromRegex(),
        dfaParserFactory.fromNFA(),
        ParseRegexToAutomataFactory.nfaParser(),
        AutomataSimulationFactory.nfaSimulation(),
        AutomataSimulationFactory.dfaSimulation(),
        AutomataGraph
    )

    WebApp(webHandler, port = 8080)


}
