package gt.tree.models.iterators

import gt.tree.INode
import gt.tree.models.Node

class InOrderTraversalIterator<T>(
    root: Node<T>
) : Iterator<Node<T>> {

    private val stack = ArrayDeque<Node<T>>()
    private var current: Node<T>? = root

    override fun hasNext(): Boolean {
        return stack.isNotEmpty() || current != null
    }

    override fun next(): Node<T> {
        while (current != null) {
            stack.addLast(current!!)
            current = current!!.left
        }

        return stack.removeLast()
            .also {
                current = it.right
            }
    }
}
