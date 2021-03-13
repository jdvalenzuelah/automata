package gt.tree

import java.util.*

interface INode<T> : Iterable<INode<T>> {

    val data: T
    val left: INode<T>?
    val right: INode<T>?
    val uid: String
}
