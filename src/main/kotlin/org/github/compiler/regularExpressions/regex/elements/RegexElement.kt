package org.github.compiler.regularExpressions.regex.elements

sealed class RegexElement(val id: Char) {
    override fun toString(): String = id.toString()
}

sealed class Operator(
    id: Char,
    val precedence: Int,
    val isUnary: Boolean = false
) : RegexElement(id) {

    object Closure : Operator('*', 3, true)

    object PositiveClosure : Operator('+', 3, true)

    object ZeroOrOne : Operator('?', 3, true)

    object Concatenation : Operator('_', 2)

    object Or : Operator('|', 1)

}

sealed class Grouping(id: Char) : RegexElement(id) {

    object OpenParenthesis : Grouping('(')

    object CloseParenthesis : Grouping(')')

}

data class Character(val char: Char) : RegexElement(char) {

    companion object {
        val EPSILON = Character(epsilon)
    }

    override fun toString(): String = id.toString()

    fun isEpsilon(): Boolean = char == epsilon

}

sealed class Augmented(id: Char) : RegexElement(id) {

    object EndMarker : Augmented('#')

}
