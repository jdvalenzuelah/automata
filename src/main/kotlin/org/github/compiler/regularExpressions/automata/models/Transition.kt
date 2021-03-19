package org.github.compiler.regularExpressions.automata.models

import org.github.compiler.regularExpressions.automata.IState
import org.github.compiler.regularExpressions.automata.ITransition

data class Transition<S, I>(
    override val edge: I,
    override val from: IState<S>,
    override val to: Collection<IState<S>>
) : ITransition<S, I>
