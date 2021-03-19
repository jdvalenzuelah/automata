package org.github.compiler.regularExpressions.syntaxTree.models

import org.github.compiler.regularExpressions.syntaxTree.INode
import org.github.compiler.regularExpressions.syntaxTree.iterators.InOrderTraversalIterator
import java.util.*

data class Node<T : Any>(
    override val data: T,
    override val left: INode<T>?,
    override val right: INode<T>?,
    override val position: Int? = null
) : INode<T> {

    override val uid: String by lazy { UUID.randomUUID().toString() }

    override fun duplicate(d: T, l: INode<T>?, r: INode<T>?, pos: Int?): INode<T> {
        return this.copy(left = l, right = r, position = pos, data = d)
    }

    override fun iterator(): Iterator<INode<T>> {
        return InOrderTraversalIterator(this)
    }

}
