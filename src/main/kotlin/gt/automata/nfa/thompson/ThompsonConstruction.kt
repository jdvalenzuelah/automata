package gt.automata.nfa.thompson

import gt.automata.nfa.models.INFA

interface ThompsonConstruction<S, I> {

    fun empty(): INFA<S, I>
    fun symbol(s: I): INFA<S, I>
    fun or(nfa1: INFA<S, I>, nfa2: INFA<S, I>): INFA<S, I>
    fun concat(nfa1: INFA<S, I>, nfa2: INFA<S, I>): INFA<S, I>
    fun closure(nfa1: INFA<S, I>): INFA<S, I>

}

