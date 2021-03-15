package gt.main

import gt.automata.AutomataSimulation
import gt.automata.IState
import gt.automata.dfa.DeterministicFiniteAutomata
import gt.automata.dfa.operations.SyntaxTreeToDfa
import gt.automata.dfa.treeOperations.SyntaxTree
import gt.automata.nfa.NonDeterministicFiniteAutomata
import gt.automata.nfa.operations.NfaToDfa
import gt.graphing.AutomataGraph
import gt.regex.AugmentRegex
import gt.regex.RegexToNFA
import gt.regex.postfix.InfixToPostfix
import gt.regex.postfix.RegexToPostfix
import gt.regex.tokenize.expression.TokenizeRegex
import gt.regex.tree.RegexToTree
import guru.nidi.graphviz.engine.Format
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.OutputStream

class AutomatasHandler<S, I>(
    private val regexTokenizer: TokenizeRegex,
    private val regexToNFA: RegexToNFA<S, I>,
    private val nfaToDfa: NfaToDfa<S, I>,
    private val dfaSimulation: AutomataSimulation<S, I, IState<S>, DeterministicFiniteAutomata<S, I>>,
    private val nfaSimulation: AutomataSimulation<S, I, Collection<IState<S>>, NonDeterministicFiniteAutomata<S, I>>,
    private val automataGraphs: AutomataGraph<S, I>,
    private val augmentRegex: AugmentRegex,
    private val regexToPostfix: RegexToPostfix,
    private val regexToTree: RegexToTree,
    private val syntaxTreeToDfa: SyntaxTreeToDfa
) {

    fun getRegexNfa(regex: String): NonDeterministicFiniteAutomata<S, I> {
        val regexExpression = regexTokenizer(regex)
        return regexToNFA(regexExpression)
    }

    // TODO: extract to service
    fun getRegexDfa(regex: String): DeterministicFiniteAutomata<String, String> {
        val tokens = regexTokenizer(AugmentRegex(regex))
        val postfix = regexToPostfix(tokens)
        val root = regexToTree(postfix)
        val tree = SyntaxTree(root, postfix)
        return syntaxTreeToDfa(tree)
    }

    fun getDfaFromNfa(nfa: NonDeterministicFiniteAutomata<S, I>): DeterministicFiniteAutomata<S, I> {
        return nfaToDfa.toDfa(nfa)
    }

    fun getNfaSimulationResult(nfa: NonDeterministicFiniteAutomata<S, I>, input: Iterable<I>): Boolean {
        return nfaSimulation.simulate(nfa, input)
    }

    fun getDfaSimulationResult(dfa: DeterministicFiniteAutomata<S, I>, input: Iterable<I>): Boolean {
        return dfaSimulation.simulate(dfa, input)
    }

    fun getNfaGraph(nfa: NonDeterministicFiniteAutomata<S, I>): ByteArrayOutputStream {
        val output = ByteArrayOutputStream()

        automataGraphs.graphFromNfa(nfa)
            .render(Format.PNG)
            .toOutputStream(output)

        return output
    }

    fun getDfaGraph(dfa: DeterministicFiniteAutomata<S, I>): ByteArrayOutputStream {
        val output = ByteArrayOutputStream()

        automataGraphs.graphFromDfa(dfa)
            .render(Format.PNG)
            .toOutputStream(output)

        return output
    }

}
