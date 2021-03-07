package gt.graphing

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import com.xenomachina.argparser.mainBody
import gt.automata.models.State
import gt.automata.nfa.epsilon
import gt.automata.nfa.operations.EpsilonClosure
import gt.automata.nfa.operations.SubSetConstruction
import gt.automata.nfa.thompson.configuration.ThomptsonTransformConfig
import gt.regex.tokenize.configuration.TokenizerConfig
import guru.nidi.graphviz.engine.Format
import java.io.File

private fun String.ensureEndsWith(suffix: String) = if(this.endsWith(suffix)) this else "$this$suffix"

private class Args(parser: ArgParser) {
    val regex by parser.storing(
        "-e", "--regex",
        help = "Regex to render as graph",
    )

    val desPath by parser.storing(
        "-o", "--output",
        help = "PNG File were output will be saved)"
    )

    val type by parser.mapping(
        "--nfa" to AutomataType.nfa,
        "--dfa" to AutomataType.dfa,
        help = "type of automata to generate from regex (nfa by default)"
    ).default(AutomataType.nfa)
}

private enum class AutomataType { nfa, dfa }

private fun getMapper() = object  {
    private var counter = 0
    fun getState(): Int = counter++
}

fun main(args: Array<String>): Unit = mainBody {
    //TODO: Remove logging warnings

    val args = ArgParser(args).parseInto(::Args)

    val regexTokenizer = TokenizerConfig.expressionTokenizer()

    val nfaGenerator = ThomptsonTransformConfig.getThompsonTransform()

    val tokenizedRegex = regexTokenizer(args.regex)
    val generatedNfa = nfaGenerator(tokenizedRegex)

    val graphing = AutomataGraph<Int, String>()

    val graph = when(args.type) {
        AutomataType.nfa -> graphing.graphFromNfa(generatedNfa)
        AutomataType.dfa -> {
            val mapper = getMapper()
            val nfaToDfa = SubSetConstruction<Int, String>(EpsilonClosure(epsilon)) { State(mapper.getState()) }
            val dfa = nfaToDfa.toDfa(generatedNfa)
            graphing.graphFromDfa(dfa)
        }
    }

    graph.render(Format.PNG)
        .toFile(File(args.desPath.ensureEndsWith(".png")))
}
