package org.github.compiler.regularExpressions.transforms.regex

import org.github.compiler.regularExpressions.automata.IState
import org.github.compiler.regularExpressions.automata.dfa.DeterministicFiniteAutomata
import org.github.compiler.regularExpressions.automata.dfa.models.DFA
import org.github.compiler.regularExpressions.automata.models.State
import org.github.compiler.regularExpressions.automata.models.builders.transitions
import org.github.compiler.regularExpressions.automata.nfa.NonDeterministicFiniteAutomata
import org.github.compiler.regularExpressions.automata.nfa.operations.EpsilonClosure
import org.github.compiler.regularExpressions.automata.nfa.operations.IdGenStrategy
import org.github.compiler.regularExpressions.regex.RegularExpression
import org.github.compiler.regularExpressions.regex.transform.RegexTransforms
import org.github.compiler.regularExpressions.transforms.Transform
import org.tinylog.kotlin.Logger

class NFAToDFA<S>(
    private val epsilonClosure: EpsilonClosure<S, Char>,
    private val stateIdGenStrategy: IdGenStrategy<S>,
    private val epsilonValue: Char
) : Transform<NonDeterministicFiniteAutomata<S, Char>, DeterministicFiniteAutomata<S, Char>> {

    private data class MarkedState<S>(var marked: Boolean, val states: Collection<IState<S>>)

    private fun NonDeterministicFiniteAutomata<S, Char>.move(states: Collection<IState<S>>, symbol: Char): Array<IState<S>> {
        return states.flatMap { move(it, symbol) ?: emptyList() }.toTypedArray()
    }

    private fun constructSubSet(nfa: NonDeterministicFiniteAutomata<S, Char>): DeterministicFiniteAutomata<S, Char> {
        Logger.info("Starting subset construct fot nfa=$nfa")
        val initialEClosure = epsilonClosure.epsilonClosure(nfa, nfa.initialState)
        val dTran = mutableMapOf<Pair<Collection<IState<S>>, Char>, Collection<IState<S>>>()
        val dStates = mutableListOf( MarkedState(false, initialEClosure) )

        do {
            val t = dStates.firstOrNull { !it.marked }

            if(t != null) {
                t.marked = true
                for(a in nfa.alphabet) {
                    if(a == epsilonValue) continue
                    val u =  epsilonClosure.epsilonClosure(nfa, *nfa.move(t.states, a))

                    if(dStates.none { it.states == u })
                        dStates.add(MarkedState(false, u))

                    dTran[t.states to a] = u
                }
            }
        } while (t != null)

        val mappedStates = mutableMapOf<Collection<IState<S>>, IState<S>>()
        val alphabet = mutableSetOf<Char>()
        val finalStates = mutableSetOf<IState<S>>()
        val transitions = transitions<S, Char> {
            dTran.forEach { (fromTran, to) ->
                if(to.isNotEmpty()) {
                    val fromState = mappedStates.getOrPut(fromTran.first) { State(stateIdGenStrategy()) }
                    val toState = mappedStates.getOrPut(to) { State(stateIdGenStrategy()) }
                    alphabet.add(fromTran.second)

                    if(to.any { it in nfa.finalStates })
                        finalStates.add(toState)

                    fromState to toState by fromTran.second
                }
            }
        }

        return DFA(alphabet, finalStates, mappedStates[initialEClosure]!!, mappedStates.values, transitions)
    }


    override fun invoke(p1: NonDeterministicFiniteAutomata<S, Char>): DeterministicFiniteAutomata<S, Char> {
        return constructSubSet(p1)
    }

}
