package org.github.compiler.regularExpressions.syntaxTree


interface IOperations<T: Any> {
    fun nullable(node: INode<T>): Boolean
    fun firstPos(node: INode<T>): Collection<Int>
    fun lastPos(node: INode<T>): Collection<Int>
    fun followPos(pos: Int): Collection<Int>
}
