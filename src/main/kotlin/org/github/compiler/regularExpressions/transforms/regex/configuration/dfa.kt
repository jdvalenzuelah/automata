package org.github.compiler.regularExpressions.transforms.regex.configuration

import org.github.compiler.regularExpressions.automata.dfa.DeterministicFiniteAutomata
import org.github.compiler.regularExpressions.automata.nfa.operations.EpsilonClosureImpl
import org.github.compiler.regularExpressions.regex.configuration.RegexToPostfix
import org.github.compiler.regularExpressions.regex.elements.epsilon
import org.github.compiler.regularExpressions.transforms.regex.NFAToDFA
import org.github.compiler.regularExpressions.regex.transform.RegexTransforms
import org.github.compiler.regularExpressions.regex.transform.augment.AugmentRegex
import org.github.compiler.regularExpressions.regex.transform.postfix.InfixRegexToPostfix
import org.github.compiler.regularExpressions.transforms.regex.SyntaxTreeToDFA
import org.github.compiler.regularExpressions.transforms.regex.RegexToSyntaxTree
import org.github.compiler.regularExpressions.transforms.regex.RegexToTree
import org.github.compiler.regularExpressions.transforms.then

object RegexToDFAFactory {

    enum class ConstructionFrom {
        Regex,
        NFA
    }

    fun regexToDFAFor(type: ConstructionFrom): RegexTransforms<DeterministicFiniteAutomata<String, Char>>  {
        return when(type) {
            ConstructionFrom.NFA ->  regexToNfa()
                .then(NFAToDFA(EpsilonClosureImpl(epsilon), LetterStateMapper, epsilon))
            ConstructionFrom.Regex -> AugmentRegex
                .then(RegexToPostfix())
                .then(RegexToSyntaxTree(RegexToTree))
                .then(SyntaxTreeToDFA(LetterStateMapper))
        }
    }
}
