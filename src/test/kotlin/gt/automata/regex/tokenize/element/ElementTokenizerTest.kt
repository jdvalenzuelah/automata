package gt.automata.regex.tokenize.element

import gt.automata.regex.element.Character
import gt.automata.regex.element.Grouping
import gt.automata.regex.element.Operator
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class ElementTokenizerTest {

    @Test
    fun `should parse * to closure`() {
        assertEquals(Operator.Closure, OperatorTokenizer("*"))
    }

    @Test
    fun `should parse + to union`() {
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
