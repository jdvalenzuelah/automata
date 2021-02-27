package gt.automata.regex.element

sealed class RegexElement

sealed class Operator(
    val id: String,
    val precedence: Int
) : RegexElement() {

    object Closure : Operator("*", 3)

    object Concatenation : Operator("", 2)

    object Or : Operator("|", 1)

}

sealed class Grouping(val id: String) : RegexElement() {

    object OpenParenthesis : Grouping("(")

    object CloseParenthesis : Grouping(")")

}


data class Character(val char: String) : RegexElement()
