package org.github.compiler.regularExpressions.transforms.regex

import org.github.compiler.regularExpressions.automata.dfa.DeterministicFiniteAutomata
import org.github.compiler.regularExpressions.regex.configuration.RegexTokenizerRef
import org.github.compiler.regularExpressions.automata.nfa.NonDeterministicFiniteAutomata
import org.github.compiler.regularExpressions.transforms.regex.configuration.RegexToDFAFactory
import org.github.compiler.regularExpressions.transforms.regex.configuration.regexToNfa

object ParseRegexToAutomataFactory {

    fun nfaParser() : (String) -> NonDeterministicFiniteAutomata<String, Char> {
        val tokenizer = RegexTokenizerRef()
        val regexToNFa = regexToNfa()
        return fun (regex : String): NonDeterministicFiniteAutomata<String, Char> = regexToNFa(tokenizer(regex))
    }

    class ParseRegexToDFAFactory {

        fun fromRegex() : (String) -> DeterministicFiniteAutomata<String, Char> {
            val tokenizer = RegexTokenizerRef()
            val regexToDFA = RegexToDFAFactory.regexToDFAFor(RegexToDFAFactory.ConstructionFrom.Regex)
            return fun (regex : String): DeterministicFiniteAutomata<String, Char> = regexToDFA(tokenizer(regex))
        }

        fun fromNFA() : (String) -> DeterministicFiniteAutomata<String, Char> {
            val tokenizer = RegexTokenizerRef()
            val regexToDFA = RegexToDFAFactory.regexToDFAFor(RegexToDFAFactory.ConstructionFrom.NFA)
            return fun (regex : String): DeterministicFiniteAutomata<String, Char> = regexToDFA(tokenizer(regex))
        }

    }

    fun dfaParser() = ParseRegexToDFAFactory()


}
