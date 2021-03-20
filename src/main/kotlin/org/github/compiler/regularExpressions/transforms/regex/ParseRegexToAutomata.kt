package org.github.compiler.regularExpressions.transforms.regex

import org.github.compiler.regularExpressions.automata.dfa.DeterministicFiniteAutomata
import org.github.compiler.regularExpressions.regex.configuration.RegexTokenizerRef
import org.github.compiler.regularExpressions.automata.nfa.NonDeterministicFiniteAutomata
import org.github.compiler.regularExpressions.transforms.Transform
import org.github.compiler.regularExpressions.transforms.regex.configuration.RegexToDFAFactory
import org.github.compiler.regularExpressions.transforms.regex.configuration.regexToNfa
import org.github.compiler.regularExpressions.transforms.then

object ParseRegexToAutomataFactory {

    fun nfaParser() : Transform<String, NonDeterministicFiniteAutomata<String, Char>> {
        return RegexTokenizerRef().then(regexToNfa())
    }

    class ParseRegexToDFAFactory {

        fun fromRegex() : Transform<String, DeterministicFiniteAutomata<String, Char>> {
            val regexToDFA = RegexToDFAFactory.regexToDFAFor(RegexToDFAFactory.ConstructionFrom.Regex)
            return RegexTokenizerRef().then(regexToDFA)
        }

        fun fromNFA() : Transform<String, DeterministicFiniteAutomata<String, Char>> {
            val regexToDFA = RegexToDFAFactory.regexToDFAFor(RegexToDFAFactory.ConstructionFrom.NFA)
            return RegexTokenizerRef().then(regexToDFA)
        }

    }

    fun dfaParser() = ParseRegexToDFAFactory()


}
