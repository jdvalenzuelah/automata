package org.github.compiler.regularExpressions.automata.nfa.operations

import org.github.compiler.regularExpressions.automata.nfa.NonDeterministicFiniteAutomata

interface ThompsonConstruction<S, I> {

    fun empty(): NonDeterministicFiniteAutomata<S, I>
    fun symbol(s: I): NonDeterministicFiniteAutomata<S, I>
    fun or(nfa1: NonDeterministicFiniteAutomata<S, I>, nfa2: NonDeterministicFiniteAutomata<S, I>): NonDeterministicFiniteAutomata<S, I>
    fun concat(nfa1: NonDeterministicFiniteAutomata<S, I>, nfa2: NonDeterministicFiniteAutomata<S, I>): NonDeterministicFiniteAutomata<S, I>
    fun closure(nfa1: NonDeterministicFiniteAutomata<S, I>): NonDeterministicFiniteAutomata<S, I>
    fun positiveClosure(nfa1: NonDeterministicFiniteAutomata<S, I>): NonDeterministicFiniteAutomata<S, I>
    fun zeroOrOne(nfa1: NonDeterministicFiniteAutomata<S, I>): NonDeterministicFiniteAutomata<S, I>

}
