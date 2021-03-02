package gt.automata.models

import gt.automata.IState

data class State<S>(override val name: S): IState<S> {
    override fun toString(): String = name.toString()
}
