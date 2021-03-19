package org.github.compiler.regularExpressions.automata.models

import org.github.compiler.regularExpressions.automata.IState

data class State<S>(override val name: S): IState<S> {
    override fun toString(): String = name.toString()
}
