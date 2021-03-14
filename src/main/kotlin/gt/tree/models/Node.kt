package gt.tree.models

import gt.tree.INode
import gt.tree.models.iterators.InOrderTraversalIterator
import java.util.*

data class Node<T>(
    override val data: T,
    override val left: Node<T>?,
    override val right: Node<T>?,
    override val position: Int? = null
) : INode<T>, Iterable<Node<T>> {

    private val uuid: String by lazy {
        UUID.randomUUID().toString()
    }

    override fun uid(): String {
        return uuid
    }

    override fun iterator(): Iterator<Node<T>> {
        return InOrderTraversalIterator(this)
    }

}


fun <T> Node<T>.left(l: Node<T>?) = copy(left = l)

fun <T> Node<T>.right(r: Node<T>?) = copy(right = r)

fun <T> Node<T>.swap() = copy(left = right, right = left)
