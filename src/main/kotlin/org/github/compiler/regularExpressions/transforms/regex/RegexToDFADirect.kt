package org.github.compiler.regularExpressions.transforms.regex

import org.github.compiler.regularExpressions.regex.elements.Character
import org.github.compiler.regularExpressions.regex.elements.Augmented
import org.github.compiler.regularExpressions.automata.dfa.DeterministicFiniteAutomata
import org.github.compiler.regularExpressions.automata.models.State
import org.github.compiler.regularExpressions.automata.models.builders.dfa
import org.github.compiler.regularExpressions.automata.nfa.operations.IdGenStrategy
import org.github.compiler.regularExpressions.regex.RegularExpression
import org.github.compiler.regularExpressions.regex.transform.RegexTransforms
import org.github.compiler.regularExpressions.syntaxTree.models.RegexSyntaxTree

class RegexToDFADirect<S>(
    private val regexToSyntaxTree: RegexTransforms<RegexSyntaxTree>,
    private val stateIdGenStrategy: IdGenStrategy<S>,
) : RegexTransforms<DeterministicFiniteAutomata<S, Char>> {

    private data class Marked(var marked: Boolean, val states: Collection<Int>)

    override fun invoke(p1: RegularExpression): DeterministicFiniteAutomata<S, Char> {
        val tree = regexToSyntaxTree(p1)

        val charPos = tree.root.mapNotNull {
            when(val data = it.data) {
                is Character -> if(data.isEpsilon()) null else  data.char to it.position!!
                is Augmented.EndMarker -> it.data.id to it.position!!
                else -> null
            }
        }
            .groupBy { it.first }

        val start = tree.firstPos(tree.root)
        val dStates = mutableListOf(Marked(false, start))
        val dTran = mutableMapOf<Pair<Collection<Int>, Char>, Collection<Int>>()
        val endMarkerPos = charPos[Augmented.EndMarker.id]?.map { it.second } ?: emptyList()
        val finalStates = mutableListOf<Collection<Int>>()

        do {
            val s = dStates.firstOrNull { !it.marked }

            if(s != null) {
                s.marked = true

                if(s.states.any { it in endMarkerPos })
                    finalStates.add(s.states)

                for(a in tree.alphabet) {
                    if(a.char == Augmented.EndMarker.id) continue
                    val aPos = charPos[a.char] ?: emptyList()
                    val u = aPos.filter { it.second in s.states }.flatMap { tree.followPos(it.second) }.toSet()

                    if(u.isEmpty()) continue

                    if(dStates.none { it.states == u })
                        dStates.add(Marked(false, u))

                    dTran[s.states to a.char] = u
                }
            }
        }while (s != null)

        val mappedStates = mutableMapOf<Collection<Int>, S>()

        // TODO: Remove all the loops!
        return dfa {

            states {
                state { mappedStates.getOrPut(start) { stateIdGenStrategy() } }
                dTran.forEach { (from, to) ->
                    state { mappedStates.getOrPut(from.first) { stateIdGenStrategy() } }
                    state { mappedStates.getOrPut(to) { stateIdGenStrategy() } }
                }
            }
            initialState(mappedStates.getOrPut(start) { stateIdGenStrategy() })
            finalStates(*finalStates.map { State(mappedStates.getOrPut(it) { stateIdGenStrategy() }) }.toTypedArray())

            transitions {
                dTran.forEach { (from, to) ->
                    mappedStates.getOrPut(from.first) { stateIdGenStrategy() } to mappedStates.getOrPut(to) { stateIdGenStrategy() }  by from.second
                }
            }

        }
    }

}
