package gt.automata.nfa.models

import gt.automata.nfa.IState

typealias Transitions<S, I> = Map<I, Collection<IState<S>>>
typealias MutableTransitions<S, I> = MutableMap<I, MutableCollection<IState<S>>>

typealias TransitionTable<S, I> = Map<IState<S>, Transitions<S, I> >
typealias MutableTransitionTable<S, I> = MutableMap<IState<S>, MutableTransitions<S, I> >

