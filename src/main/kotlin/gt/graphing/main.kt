package gt.graphing

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.mainBody
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
}

fun main(args: Array<String>): Unit = mainBody {
    //TODO: Remove logging warnings

    val args = ArgParser(args).parseInto(::Args)


    val regexTokenizer = TokenizerConfig.expressionTokenizer()

    val nfaGenerator = ThomptsonTransformConfig.getThompsonTransform()

    val tokenizedRegex = regexTokenizer(args.regex)
    val generatedNfa = nfaGenerator(tokenizedRegex)

    AutomataGraph<Int, String>().graphFromNfa(generatedNfa)
        .render(Format.PNG)
        .toFile(File(args.desPath.ensureEndsWith(".png")))
}
