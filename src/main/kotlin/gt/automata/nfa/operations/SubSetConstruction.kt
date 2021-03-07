package gt.automata.nfa.operations

import gt.automata.IState
import gt.automata.dfa.DeterministicFiniteAutomata
import gt.automata.dfa.models.DFA
import gt.automata.dfa.models.MutableTransitionTable
import gt.automata.nfa.NonDeterministicFiniteAutomata
import gt.automata.nfa.epsilon

fun interface NfaToDfa<S, I> {
    fun toDfa(nfa: NonDeterministicFiniteAutomata<S, I>): DeterministicFiniteAutomata<S, I>
}

class SubSetConstruction<S, I>(
    private val closure: EpsilonClosure<S, I>,
    private val mappingStrategy: (Collection<IState<S>>) -> IState<S>
): NfaToDfa<S, I> {

    private data class MarkedState<S>(var marked: Boolean, val states: Collection<IState<S>>)

    private fun NonDeterministicFiniteAutomata<S, I>.move(states: Collection<IState<S>>, symbol: I): Array<IState<S>> {
        return states.flatMap { move(it, symbol) ?: emptyList() }.toTypedArray()
    }

    private fun constructSubSet(nfa: NonDeterministicFiniteAutomata<S, I>): DeterministicFiniteAutomata<S, I> {
        val initialEClosure = closure.eClosure(nfa, nfa.initialState)
        val dTran = mutableMapOf<Pair<Collection<IState<S>>, I>, Collection<IState<S>>>()
        val dStates = mutableListOf( MarkedState(false, initialEClosure) )

        do {
            val t = dStates.firstOrNull { !it.marked }

            if(t != null) {
                t.marked = true
                for(a in nfa.alphabet) {
                    if(a == epsilon) continue
                    val u = closure.eClosure(nfa, *nfa.move(t.states, a))

                    if(dStates.none { it.states == u })
                        dStates.add(MarkedState(false, u))

                    dTran[t.states to a] = u
                }
            }
        } while (t != null)

        val mappedStates = mutableMapOf<Collection<IState<S>>, IState<S>>()

        val transition: MutableTransitionTable<S, I> = mutableMapOf()
        val alphabet = mutableSetOf<I>()
        dTran.forEach { (fromAndSymbol, to) ->
            if(to.isNotEmpty()) {
                val fromState = mappedStates.computeIfAbsent(fromAndSymbol.first, mappingStrategy)
                val inputSymbol = fromAndSymbol.second
                val toState = mappedStates.computeIfAbsent(to, mappingStrategy)
                if(fromAndSymbol.first.any { it in nfa.finalStates })
                alphabet.add(inputSymbol)
                transition[fromState]
                    ?.let { it[inputSymbol] = toState }
                    ?: run { transition[fromState] = mutableMapOf(inputSymbol to toState) }
            }
        }

        return DFA(
            mappedStates.values,
            mappedStates[initialEClosure]!!,
            mappedStates.filter { s -> s.key.any { it in nfa.finalStates } }.values,
            transition,
            alphabet
        )
    }

    override fun toDfa(nfa: NonDeterministicFiniteAutomata<S, I>): DeterministicFiniteAutomata<S, I> {
        return constructSubSet(nfa)
    }

}
