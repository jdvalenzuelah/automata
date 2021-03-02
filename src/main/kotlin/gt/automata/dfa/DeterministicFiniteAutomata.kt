package gt.automata.dfa

import gt.automata.Automata
import gt.automata.IState
import gt.automata.dfa.models.TransitionTable

interface DeterministicFiniteAutomata<S, I> : Automata<S, I, IState<S>> {
    val transitionTable: TransitionTable<S, I>
}
