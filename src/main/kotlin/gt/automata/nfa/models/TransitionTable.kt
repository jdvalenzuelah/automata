package gt.automata.nfa.models

import gt.automata.nfa.IState

typealias Transitions<S, I> = Map<I, IState<S>> // TODO: Allow more than one transition per input?
typealias MutableTransitions<S, I> = MutableMap<I, IState<S>>

typealias TransitionTable<S, I> = Map<IState<S>, Transitions<S, I> >
typealias MutableTransitionTable<S, I> = MutableMap<IState<S>, MutableTransitions<S, I> >

