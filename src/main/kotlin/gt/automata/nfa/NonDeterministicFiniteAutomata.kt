package gt.automata.nfa

import gt.automata.Automata
import gt.automata.IState
import gt.automata.nfa.models.TransitionTable

interface NonDeterministicFiniteAutomata<S, I> : Automata<S, I, Collection<IState<S>>> {
    val transitionTable: TransitionTable<S, I>
}
