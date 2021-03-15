package gt.regex.element

import gt.automata.nfa.epsilon

sealed class RegexElement

sealed class Operator(
    val id: String,
    val precedence: Int,
    val isUnary: Boolean = false
) : RegexElement() {

    override fun toString(): String = id

    object Closure : Operator("*", 3, true)

    object PositiveClosure : Operator("+", 3, true)

    object ZeroOrOne : Operator("?", 3, true)

    object Concatenation : Operator("", 2)

    object Or : Operator("|", 1)

}

sealed class Grouping(val id: String) : RegexElement() {

    override fun toString(): String = id

    object OpenParenthesis : Grouping("(")

    object CloseParenthesis : Grouping(")")

}

data class Character(val char: String) : RegexElement() {
    override fun toString(): String = char

    companion object {
        val EPSILON = Character(epsilon)
    }

}

sealed class Augmented(val id: String) : RegexElement() {

    override fun toString(): String = id

    object EndMarker : Augmented("#")
}
