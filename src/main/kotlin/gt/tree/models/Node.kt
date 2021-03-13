package gt.tree.models

import gt.tree.INode
import gt.tree.models.iterators.InOrderTraversalIterator
import java.util.*

data class Node<T>(
    override val data: T,
    override val left: INode<T>?,
    override val right: INode<T>?,
) : INode<T> {

    override val uid: String by lazy {
        UUID.randomUUID().toString()
    }

    override fun iterator(): Iterator<INode<T>> {
        return InOrderTraversalIterator(this)
    }

}


fun <T> Node<T>.left(l: T) = copy(left = Node(l, null, null))

fun <T> Node<T>.right(r: T) = copy(right = Node(r, null, null))
