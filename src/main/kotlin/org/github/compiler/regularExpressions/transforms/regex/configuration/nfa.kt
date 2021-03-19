package org.github.compiler.regularExpressions.transforms.regex.configuration

import org.github.compiler.regularExpressions.automata.nfa.NonDeterministicFiniteAutomata
import org.github.compiler.regularExpressions.automata.nfa.operations.IdGenStrategy
import org.github.compiler.regularExpressions.automata.nfa.operations.ThompsonConstruction
import org.github.compiler.regularExpressions.regex.transform.postfix.InfixRegexToPostfix
import org.github.compiler.regularExpressions.automata.nfa.operations.ThompsonImpl
import org.github.compiler.regularExpressions.regex.elements.epsilon
import org.github.compiler.regularExpressions.transforms.regex.RegexToNFA
import org.github.compiler.regularExpressions.regex.RegularExpression
import org.github.compiler.regularExpressions.regex.transform.RegexTransforms

class RegexToNFABuilder {
    var thompson: ThompsonConstruction<String, Char> = ThompsonImpl(epsilon, LetterStateMapper)
    var postfixConverter: RegexTransforms<RegularExpression> = InfixRegexToPostfix

    fun build(): RegexTransforms<NonDeterministicFiniteAutomata<String, Char>> = RegexToNFA(postfixConverter, thompson)

}

inline fun regexToNfa(init: RegexToNFABuilder.() -> Unit = {}) = RegexToNFABuilder().apply(init).build()
