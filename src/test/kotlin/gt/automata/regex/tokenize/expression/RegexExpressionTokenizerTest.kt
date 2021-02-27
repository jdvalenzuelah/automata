package gt.automata.regex.tokenize.expression

import gt.automata.regex.element.Character
import gt.automata.regex.element.Grouping
import gt.automata.regex.element.Operator
import gt.automata.regex.tokenize.configuration.TokenizerConfig
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
        val expected = listOf(Character("a"), Operator.Union, Character("b")).toTypedArray()
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
    fun `should tokenize regex with all operation with concatenation`() {
        val expression = "(b|b)*abb(a|b)*"

        val expected = listOf(
            Grouping.OpenParenthesis,
            Character("b"),
            Operator.Union,
            Character("b"),
            Grouping.CloseParenthesis,
            Operator.Closure,
            Character("a"),
            Operator.Concatenation,
            Character("b"),
            Operator.Concatenation,
            Character("b"),
            Grouping.OpenParenthesis,
            Character("a"),
            Operator.Union,
            Character("b"),
            Grouping.CloseParenthesis,
            Operator.Closure,
        )
            .toTypedArray()

        println()
        assertArrayEquals(expected, tokenizer(expression).toTypedArray())
    }


}
