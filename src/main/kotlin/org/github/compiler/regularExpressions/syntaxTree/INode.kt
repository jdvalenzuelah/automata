package org.github.compiler.regularExpressions.syntaxTree

interface INode<T : Any> : Iterable<INode<T>> {
    val data: T
    val left: INode<T>?
    val right: INode<T>?
    val position: Int?
    val uid: String
    fun duplicate(d: T = data, l: INode<T>? = left, r: INode<T>? = right, pos: Int? = position): INode<T>
    fun swap(): INode<T> = duplicate(l = right, r = left)
}
