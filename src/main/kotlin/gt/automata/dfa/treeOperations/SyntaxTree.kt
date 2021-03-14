package gt.automata.dfa.treeOperations

import gt.automata.nfa.epsilon
import gt.regex.element.*
import gt.tree.models.Node
import gt.tree.models.swap

// TODO: Support all operators
class SyntaxTree (
    private val root: Node<RegexElement>
) : TreeOperations {

    data class FirstAndLastPos(val firstpos: Collection<Int>, val lastpos: Collection<Int>)

    private val firstLastPosTable: Map<Node<RegexElement>, FirstAndLastPos> by lazy { buildFirstLastPos()  }
    private val followPosTable: Map<Int, MutableSet<Int>> by lazy { buildFollowPos() }

    private fun buildFirstLastPos(): Map<Node<RegexElement>, FirstAndLastPos> {
        return root.map { node ->
            node to FirstAndLastPos(firstpos(node), lastpos(node))
        }.toMap()
    }

    private fun buildFollowPos(): Map<Int, MutableSet<Int>> {
        val tmp: MutableMap<Int, MutableSet<Int>> = mutableMapOf()
        root.mapNotNull { node ->
            when(node.data) {
                is Operator.Concatenation -> {
                    val lastpos = firstLastPosTable[node.left!!]!!.lastpos
                    val firstpos = firstLastPosTable[node.right!!]!!.firstpos
                    for(i in lastpos) {
                        val existing = tmp[i]?.toMutableSet() ?: mutableSetOf()
                        existing.addAll(firstpos)
                        tmp[i] = existing
                    }
                }
                is Operator.Closure -> {
                    val (firstpos, lastpos) = firstLastPosTable[node]!!
                    for(i in lastpos) {
                        val existing = tmp[i]?.toMutableSet() ?: mutableSetOf()
                        existing.addAll(firstpos)
                        tmp[i] = existing
                    }
                }
                else -> null
            }
        }
        return tmp
    }

    override fun nullable(node: Node<RegexElement>): Boolean {
        return when(val regexElement = node.data) {
            is Character -> regexElement.char == epsilon
            is Operator.Or -> node.left?.let { nullable(it) } == true || node.right?.let { nullable(it) } == true
            is Operator.Concatenation -> node.left?.let { nullable(it) } == true && node.right?.let { nullable(it) } == true
            is Operator.Closure -> true
            else -> false
        }
    }

    override fun firstpos(node: Node<RegexElement>): Collection<Int> {
        return when(val regexElement = node.data) {
            is Character -> if(regexElement.char == epsilon) emptySet() else setOf(node.position!!)
            is Augmented -> setOf(node.position!!)
            is Operator.Or -> {
                val left = node.left?.let { firstpos(it) } ?: emptySet()
                val right = node.right?.let { firstpos(it) } ?: emptySet()
                right union left
            }
            is Operator.Concatenation -> {
                val isFirstNullable = node.left?.let { nullable(it) } ?: false
                if(isFirstNullable) {
                    val left = node.left?.let { firstpos(it) } ?: emptySet()
                    val right = node.right?.let { firstpos(it) } ?: emptySet()
                    right union left
                } else {
                    node.left?.let { firstpos(it) } ?: emptySet()
                }
            }
            is Operator.Closure -> node.left?.let { firstpos(it) } ?: emptySet()
            else -> emptySet()
        }
    }

    override fun lastpos(node: Node<RegexElement>): Collection<Int> {
        val regexElement = node.data
        val nodeToProcess = if(regexElement is Operator && !regexElement.isUnary) node.swap() else node
        return firstpos(nodeToProcess)
    }

    override fun followpos(pos: Int): Collection<Int> {
        return followPosTable[pos] ?: emptySet()
    }

}
