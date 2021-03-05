package gt.regex.tokenize.expression

import gt.regex.element.Character
import gt.regex.element.Grouping
import gt.regex.element.Operator
import gt.regex.tokenize.configuration.TokenizerConfig
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class RegexExpressionTokenizerTest {

    private val tokenizer = TokenizerConfig.expressionTokenizer()

    @Test
    fun `should tokenize regex expression with closure`() {
        val expression = "a*"
        val expected = listOf(Character("a"), Operator.Closure).toTypedArray()
       assertArrayEquals(expected, tokenizer(expression).toTypedArray())
    }

    @Test
    fun `should tokenize regex expression with union`() {
        val expression = "a|b"
        val expected = listOf(Character("a"), Operator.Or, Character("b")).toTypedArray()
        assertArrayEquals(expected, tokenizer(expression).toTypedArray())
    }

    @Test
    fun `should tokenize regex expression with concatenation`() {
        val expression = "ab"
        val expected = listOf(Character("a"), Operator.Concatenation, Character("b")).toTypedArray()
        assertArrayEquals(expected, tokenizer(expression).toTypedArray())
    }

    @Test
    fun `should tokenize regex parenthesis with concatenation`() {
        val expression = "(ab)"
        val expected = listOf(Grouping.OpenParenthesis, Character("a"), Operator.Concatenation, Character("b"), Grouping.CloseParenthesis)
            .toTypedArray()

        assertArrayEquals(expected, tokenizer(expression).toTypedArray())
    }

    @Test
    fun `should tokenize regex with all operations`() {
        val expression = "a(a|b)*b+a?"

        val expected = listOf(
            Character("a"),
            Operator.Concatenation,
            Grouping.OpenParenthesis,
            Character("a"),
            Operator.Or,
            Character("b"),
            Grouping.CloseParenthesis,
            Operator.Closure,
            Operator.Concatenation,
            Character("b"),
            Operator.PositiveClosure,
            Character("a"),
            Operator.ZeroOrOne
        )
            .toTypedArray()

        assertArrayEquals(expected, tokenizer(expression).toTypedArray())
    }

    @Test
    fun `should add concat between 2 parenthesis groups`() {
        val expression = "(a|b)(c|d)"

        val expected = listOf(
            Grouping.OpenParenthesis,
            Character("a"),
            Operator.Or,
            Character("b"),
            Grouping.CloseParenthesis,
            Operator.Concatenation,
            Grouping.OpenParenthesis,
            Character("c"),
            Operator.Or,
            Character("d"),
            Grouping.CloseParenthesis,

            )
            .toTypedArray()

        assertArrayEquals(expected, tokenizer(expression).toTypedArray())
    }


}
