package gt.automata.nfa.models

import gt.automata.nfa.IState

data class State<S>(override val name: S): IState<S>
