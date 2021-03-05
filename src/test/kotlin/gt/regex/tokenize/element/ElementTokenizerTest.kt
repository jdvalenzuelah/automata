package gt.regex.tokenize.element

import gt.regex.element.Character
import gt.regex.element.Grouping
import gt.regex.element.Operator
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class ElementTokenizerTest {

    @Test
    fun `should parse * to closure`() {
        assertEquals(Operator.Closure, OperatorTokenizer("*"))
    }

    @Test
    fun `should parse ? to positive closure`() {
        assertEquals(Operator.PositiveClosure, OperatorTokenizer("+"))
    }

    @Test
    fun `should parse ? to zero or one`() {
        assertEquals(Operator.ZeroOrOne, OperatorTokenizer("?"))
    }

    @Test
    fun `should parse | to union`() {
        assertEquals(Operator.Or, OperatorTokenizer("|"))
    }

    @Test
    fun `should parse empty string to concatenation`() {
        assertEquals(Operator.Concatenation, OperatorTokenizer(""))
    }

    @Test
    fun `should parse ( to open parenthesis`() {
        assertEquals(Grouping.OpenParenthesis, GroupingTokenizer("("))
    }

    @Test
    fun `should parse ) to open parenthesis`() {
        assertEquals(Grouping.CloseParenthesis, GroupingTokenizer(")"))
    }

    @Test
    fun `should parse any character to character`() {
        assertEquals(Character("a"), CharacterTokenizer("a"))
    }

}
