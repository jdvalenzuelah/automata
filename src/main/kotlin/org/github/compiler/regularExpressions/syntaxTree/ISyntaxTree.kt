package org.github.compiler.regularExpressions.syntaxTree

interface ISyntaxTree<T : Any> : IOperations<T> {
    val root: INode<T>
    val alphabet: Collection<T>
}
