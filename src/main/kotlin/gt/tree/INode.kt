package gt.tree

import java.util.*

interface INode<T> {

    val data: T
    val left: INode<T>?
    val right: INode<T>?
    val position: Int?

    fun uid(): String
}
