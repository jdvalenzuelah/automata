package gt.tree.models.iterators

import gt.tree.INode

class InOrderTraversalIterator<T>(
    root: INode<T>
) : Iterator<INode<T>> {

    private val stack = ArrayDeque<INode<T>>()
    private var current: INode<T>? = root

    override fun hasNext(): Boolean {
        return stack.isNotEmpty() || current != null
    }

    override fun next(): INode<T> {
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
