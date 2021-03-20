# Architecture

Based on a dependency injection model to allow separation of concerns and increase the re-use of existing code and models.

## Regular expressions

A regular expression is defined as a collection of `RegexElements` which is the smallest unit a regular expression can be defined as.

```kotlin
typealias RegularExpression = Collection<RegexElement>
```

To transform a string into a regular expression a `RegexTokenizer` is used, this regex tokenizer is dependent on tokenization of `RegexElements`

### Tokenization

Tokenization of a `RegexElement` is defined as
```kotlin
fun interface RegexElementTokenizer<out T: RegexElement> : Transform<Char, T?>
```

Tokenization of a `RegexExpression` is defined as
```kotlin
typealias RegexTokenizer = Transform<String, RegularExpression>
```

This model allows interchangeability of implementations for each tokenizer.

## Automatas

An automata is defined as
```kotlin
interface Automata<S, I, O> : Iterable<ITransition<S, I>> {

    val states: Collection<IState<S>> // S

    val initialState: IState<S> // So

    val finalStates: Collection<IState<S>> // F

    val alphabet: Collection<I>

    fun move(state: IState<S>, char: I): O? // Transition function

}
```

It comes in 2 variants: Deterministic and NonDeterministic
```kotlin
interface NonDeterministicFiniteAutomata<S, I> : Automata<S, I, Collection<IState<S>>> {
    val transitionTable: TransitionTable<S, I>
}

interface DeterministicFiniteAutomata<S, I> : Automata<S, I, IState<S>> {
    val transitionTable: TransitionTable<S, I>
}
```

## Transforms

A transform is defined as:

```kotlin
fun interface Transform<in I, out O>: (I) -> O
```

This abstraction allows applying transformations sequentially, this eliminates the need to have a transform from type A to type C directly, this allows to leverage existing transformations to be used as intermediate steps, so a transform can be generated from a type A to type B transform and a type B to type C resulting on a transform from type A to type C that internally is transformed to type B.


To ease the handling of chained transforms a helper function is included:

```kotlin
fun <A, B, D> Transform<A, B>.then(next: Transform<B, D> ) = Transform<A, D> { next(this(it)) }
```

Example
```kotlin

val stringToGtq = Transform<String, BigDecimal> { it.toBigDecimal() }

val gtqToUsd= Transform<BigDecimal, BigDecimal>  { it.multiply("7.59".toBigDecimal()) }

val usdToCents = Transform<BigDecimal, BigDecimal>  { it.multiply(100.toBigDecimal()) }

val gtqToUsdCents : Transform<String, BigDecimal> = stringToGtq.then(gtqToUsd).then(usdToCents)

gtqToUsdCents("1") // 759.00

```

These transforms are leveraged across the project to reduce the needed code and to transform regular expression to automatas
