package gt.automata.dfa.treeOperations

import gt.regex.element.RegexElement
import gt.tree.models.Node

interface TreeOperations {
    fun nullable(node: Node<RegexElement>): Boolean
    fun firstpos(node: Node<RegexElement>): Collection<Int>
    fun lastpos(node: Node<RegexElement>): Collection<Int>
    fun followpos(pos: Int): Collection<Int>
}
