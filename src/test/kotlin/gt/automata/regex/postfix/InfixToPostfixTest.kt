package gt.automata.regex.postfix

import gt.automata.regex.RegexExpression
import gt.automata.regex.element.Character
import gt.automata.regex.element.Grouping
import gt.automata.regex.element.Operator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.lang.IllegalStateException

class InfixToPostfixTest {

    @Test
    fun `should convert infix regex to postfix`() {
        /* a(a|b) */
        val infixRegex: RegexExpression = listOf(
            Character("a"),
            Operator.Concatenation,
            Grouping.OpenParenthesis,
            Character("a"),
            Operator.Or,
            Character("b"),
            Grouping.CloseParenthesis
        )

        val expectedPostfixRegex: RegexExpression = listOf(
            Character("a"),
            Character("a"),
            Character("b"),
            Operator.Or,
            Operator.Concatenation
        )

        Assertions.assertArrayEquals(expectedPostfixRegex.toTypedArray(), RegexToPostfix(infixRegex).toTypedArray())

    }

    @Test
    fun `should throw error if closing parenthesis is included without opening`() {
        val infixRegex: RegexExpression = listOf(
            Character("a"),
            Operator.Concatenation,
            Grouping.CloseParenthesis,
            Character("a"),
            Operator.Or,
            Character("b"),
        )

        Assertions.assertThrows(IllegalStateException::class.java) {
            RegexToPostfix(infixRegex)
        }

    }

}
