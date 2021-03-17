package gt.automata.dfa.operations

import gt.automata.dfa.DeterministicFiniteAutomata
import gt.automata.dfa.models.dfa
import gt.automata.dfa.treeOperations.ISyntaxTree
import gt.regex.element.Character
import gt.regex.element.Augmented


fun interface SyntaxTreeToDeterministicFiniteAutomata : (ISyntaxTree) -> DeterministicFiniteAutomata<Int, String>


// TODO: Add mapping strategy and use generics instead of <String, String>
object SyntaxTreeToDfa : SyntaxTreeToDeterministicFiniteAutomata {

    private var letters = (0..Int.MAX_VALUE).iterator()
    private fun getState(): Int {
        return letters.next()
    }

    private data class Marked(var marked: Boolean, val states: Collection<Int>)

    override fun invoke(tree: ISyntaxTree): DeterministicFiniteAutomata<Int, String> {
        val charPos = tree.root.mapNotNull {
            when(it.data) {
                is Character -> if(it.data == Character.EPSILON) null else  it.data.char to it.position!!
                is Augmented.EndMarker -> it.data.id to it.position!!
                else -> null
            }
        }
            .groupBy { it.first }

        val start = tree.firstpos(tree.root)
        val dStates = mutableListOf(Marked(false, start))
        val dTran = mutableMapOf<Pair<Collection<Int>, String>, Collection<Int>>()
        val endMarkerPos = charPos[Augmented.EndMarker.id]?.map { it.second } ?: emptyList()
        val finalStates = mutableListOf<Collection<Int>>()

        do {
            val s = dStates.firstOrNull { !it.marked }

            if(s != null) {
                s.marked = true

                if(s.states.any { it in endMarkerPos })
                    finalStates.add(s.states)

                for(a in tree.alphabet) {
                    if(a == Augmented.EndMarker.id) continue
                    val aPos = charPos[a] ?: emptyList()
                    val u = aPos.filter { it.second in s.states }.flatMap { tree.followpos(it.second) }.toSet()

                    if(u.isEmpty()) continue

                    if(dStates.none { it.states == u })
                        dStates.add(Marked(false, u))

                    dTran[s.states to a] = u
                }
            }
        }while (s != null)

        val mappedStates = mutableMapOf<Collection<Int>, Int>()

        // TODO: Remove all the loops!
        return dfa {

            states {
                dTran.forEach { (from, to) ->
                    state { mappedStates.getOrPut(from.first) { getState() } }
                    state { mappedStates.getOrPut(to) { getState() } }
                }
            }
            initialState(mappedStates.getOrPut(start) { getState() })
            finalStates(*finalStates.map { mappedStates.getOrPut(it) { getState() } }.toTypedArray())

            transitions {
                dTran.forEach { (from, to) ->
                    mappedStates.getOrPut(from.first) { getState() } to mappedStates.getOrPut(to) { getState() }  by from.second
                }
            }

        }

    }
}
