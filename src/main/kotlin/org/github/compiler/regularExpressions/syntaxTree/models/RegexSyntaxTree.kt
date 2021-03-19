package org.github.compiler.regularExpressions.syntaxTree.models

import org.github.compiler.regularExpressions.regex.RegularExpression
import org.github.compiler.regularExpressions.regex.elements.Augmented
import org.github.compiler.regularExpressions.regex.elements.Character
import org.github.compiler.regularExpressions.regex.elements.Operator
import org.github.compiler.regularExpressions.syntaxTree.ISyntaxTree
import org.github.compiler.regularExpressions.regex.elements.RegexElement
import org.github.compiler.regularExpressions.syntaxTree.INode

data class RegexSyntaxTree(
    override val root: INode<RegexElement>,
    val regexExpression: RegularExpression
) : ISyntaxTree<RegexElement> {

    data class FirstAndLastPos(val firstPos: Collection<Int>, val lastPos: Collection<Int>)

    private val firstLastPosTable: Map<INode<RegexElement>, FirstAndLastPos> by lazy { buildFirstLastPos()  }
    private val followPosTable: Map<Int, MutableSet<Int>> by lazy { buildFollowPos() }

    private fun buildFirstLastPos(): Map<INode<RegexElement>, FirstAndLastPos> {
        return root.map { node ->
            node to FirstAndLastPos(firstPos(node), firstPos(node))
        }.toMap()
    }

    private fun buildFollowPos(): Map<Int, MutableSet<Int>> {
        val tmp: MutableMap<Int, MutableSet<Int>> = mutableMapOf()
        root.mapNotNull { node ->
            when(node.data) {
                is Operator.Concatenation -> {
                    val lastPos = firstLastPosTable[node.left!!]!!.lastPos
                    val firstPos = firstLastPosTable[node.right!!]!!.firstPos
                    for(i in lastPos) {
                        val existing = tmp[i]?.toMutableSet() ?: mutableSetOf()
                        existing.addAll(firstPos)
                        tmp[i] = existing
                    }
                }
                is Operator.Closure -> {
                    val (firstPos, lastPos) = firstLastPosTable[node]!!
                    for(i in lastPos) {
                        val existing = tmp[i]?.toMutableSet() ?: mutableSetOf()
                        existing.addAll(firstPos)
                        tmp[i] = existing
                    }
                }
                else -> null
            }
        }
        return tmp
    }

    override val alphabet: Collection<Character> by lazy { regexExpression.filterIsInstance(Character::class.java) }

    override fun firstPos(node: INode<RegexElement>): Collection<Int> {
        return when(val regexElement = node.data) {
            is Character -> if(regexElement.isEpsilon()) emptySet() else setOf(node.position!!)
            is Augmented -> setOf(node.position!!)
            is Operator.Or -> {
                val left = node.left?.let { firstPos(it) } ?: emptySet()
                val right = node.right?.let { firstPos(it) } ?: emptySet()
                right union left
            }
            is Operator.Concatenation -> {
                val isFirstNullable = node.left?.let { nullable(it) } ?: false
                if(isFirstNullable) {
                    val left = node.left?.let { firstPos(it) } ?: emptySet()
                    val right = node.right?.let { firstPos(it) } ?: emptySet()
                    right union left
                } else {
                    node.left?.let { firstPos(it) } ?: emptySet()
                }
            }
            is Operator.Closure -> node.left?.let { firstPos(it) } ?: emptySet()
            else -> emptySet()
        }.sorted()
    }

    override fun followPos(pos: Int): Collection<Int> {
        return (followPosTable[pos] ?: emptySet()).sorted()
    }

    override fun lastPos(node: INode<RegexElement>): Collection<Int> {
        val regexElement = node.data
        val nodeToProcess = if(regexElement is Operator && !regexElement.isUnary) node.swap() else node
        return firstPos(nodeToProcess).sorted()
    }

    override fun nullable(node: INode<RegexElement>): Boolean {
        return when(val regexElement = node.data) {
            is Character -> regexElement.isEpsilon()
            is Operator.Or -> node.left?.let { nullable(it) } == true || node.right?.let { nullable(it) } == true
            is Operator.Concatenation -> node.left?.let { nullable(it) } == true && node.right?.let { nullable(it) } == true
            is Operator.Closure -> true
            else -> false
        }
    }


}
