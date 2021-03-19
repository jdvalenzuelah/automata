package org.github.compiler.cli.regex

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import com.xenomachina.argparser.mainBody
import guru.nidi.graphviz.engine.Format
import org.github.compiler.regularExpressions.automata.Automata
import org.github.compiler.regularExpressions.automata.configuration.AutomataSimulationFactory
import org.github.compiler.regularExpressions.automata.dfa.DeterministicFiniteAutomata
import org.github.compiler.regularExpressions.automata.nfa.NonDeterministicFiniteAutomata
import org.github.compiler.regularExpressions.automata.toFileFormat
import org.github.compiler.regularExpressions.graph.AutomataGraph
import org.github.compiler.regularExpressions.transforms.regex.ParseRegexToAutomataFactory
import org.github.compiler.regularExpressions.transforms.regex.configuration.RegexToDFAFactory
import org.tinylog.kotlin.Logger
import java.io.File

private fun String.ensureEndsWith(suffix: String) = if(this.endsWith(suffix)) this else "$this$suffix"


private class Args(parser: ArgParser) {
    val regex by parser.storing(
        "-e", "--regex",
        help = "Regex to render as graph",
    )

    val test by parser.storing(
        "-t", "--test",
        help = "Perform automata simulation with given string"
    ).default("")

    val desPath by parser.storing(
        "-o", "--output",
        help = "PNG File were output will be saved)"
    ).default("")

    val type by parser.mapping(
        "--nfa" to AutomataType.nfa,
        "--dfa" to AutomataType.dfa,
        help = "type of automata to generate from regex (nfa by default)"
    ).default(AutomataType.nfa)

    val dfaParsing by parser.mapping(
        "--direct" to RegexToDFAFactory.ConstructionFrom.Regex,
        "--subset" to RegexToDFAFactory.ConstructionFrom.NFA,
        help = "Type of conversion to parse regex into dfa"
    ).default(RegexToDFAFactory.ConstructionFrom.NFA)

    val export by parser.storing(
        "-x", "--export",
        help = "Export dfa to txt file using default file format"
    ).default("")

}

private enum class AutomataType { nfa, dfa }

private fun handleNFA(nfa: NonDeterministicFiniteAutomata<String, Char>, args: Args) {
    if(args.test.isNotEmpty()) {
        val res = AutomataSimulationFactory.nfaSimulation().simulate(nfa, args.test.asIterable())
        println("${args.test}: $res")
    }
}


private fun handleDFA(dfa: DeterministicFiniteAutomata<String, Char>, args: Args) {
    if(args.test.isNotEmpty()) {
        val res = AutomataSimulationFactory.dfaSimulation().simulate(dfa, args.test.asIterable())
        println("${args.test}: $res")
    }
}

private fun exportAutomata(automata: Automata<*,*,*>, path: String) {
    File(path.ensureEndsWith(".txt")).writeText(automata.toFileFormat())
}

private inline fun <reified T> gracefullyFail(block: () -> T): T? {
    return try {
        block()
    } catch (e: Exception) {
        Logger.error("error $e")
        println("An error has occurred during execution, please check logs for more info")
        null
    }
}

fun main(args: Array<String>): Unit = mainBody {
    val arguments = ArgParser(args).parseInto(::Args)

    gracefullyFail {
        val automata = when(arguments.type) {
            AutomataType.nfa -> {
                ParseRegexToAutomataFactory.nfaParser()(arguments.regex).also { handleNFA(it, arguments) }
            }
            AutomataType.dfa -> {
                val factory = ParseRegexToAutomataFactory.dfaParser()
                when(arguments.dfaParsing) {
                    RegexToDFAFactory.ConstructionFrom.Regex -> factory.fromRegex()(arguments.regex)
                    else -> factory.fromNFA()(arguments.regex)
                }.also { handleDFA(it, arguments) }
            }
        }

        if(arguments.desPath.isNotEmpty())
            AutomataGraph.graph(automata)
                .render(Format.PNG)
                .toFile(File(arguments.desPath.ensureEndsWith(".png")))

        if(arguments.export.isNotEmpty())
            exportAutomata(automata, arguments.export)
    }
}
