package org.github.compiler.regularExpressions.transforms.regex.configuration

import guru.nidi.graphviz.engine.Format
import org.github.compiler.regularExpressions.automata.dfa.DeterministicFiniteAutomata
import org.github.compiler.regularExpressions.automata.nfa.operations.EpsilonClosureImpl
import org.github.compiler.regularExpressions.graph.AutomataGraph
import org.github.compiler.regularExpressions.regex.configuration.RegexToPostfix
import org.github.compiler.regularExpressions.regex.elements.epsilon
import org.github.compiler.regularExpressions.transforms.regex.RegexToDFAByNFA
import org.github.compiler.regularExpressions.regex.elements.Character
import org.github.compiler.regularExpressions.regex.elements.Operator
import org.github.compiler.regularExpressions.regex.transform.RegexTransforms
import org.github.compiler.regularExpressions.transforms.regex.RegexToDFADirect
import org.github.compiler.regularExpressions.transforms.regex.RegexToSyntaxTree
import org.github.compiler.regularExpressions.transforms.regex.RegexToTree
import java.io.File

object RegexToDFAFactory {

    enum class ConstructionFrom {
        Regex,
        NFA
    }

    fun regexToDFAFor(type: ConstructionFrom): RegexTransforms<DeterministicFiniteAutomata<String, Char>>  {
        return when(type) {
            ConstructionFrom.NFA -> RegexToDFAByNFA(regexToNfa(), EpsilonClosureImpl(epsilon), LetterStateMapper, epsilon)
            ConstructionFrom.Regex -> RegexToDFADirect(RegexToSyntaxTree(RegexToTree(RegexToPostfix())), LetterStateMapper)
        }
    }
}
