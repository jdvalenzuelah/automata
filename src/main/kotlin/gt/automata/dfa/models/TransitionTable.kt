package gt.automata.dfa.models

import gt.automata.IState

typealias TransitionTable<S, I> = Map<IState<S>, Map<I, IState<S>>>
typealias MutableTransitionTable<S, I> = MutableMap<IState<S>, MutableMap<I, IState<S>>>
