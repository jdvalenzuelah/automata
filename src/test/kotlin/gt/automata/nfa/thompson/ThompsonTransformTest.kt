package gt.automata.nfa.thompson

import gt.automata.nfa.epsilon
import gt.automata.nfa.models.nfa
import gt.automata.nfa.thompson.configuration.ThomptsonTransformConfig
import gt.automata.regex.element.Character
import gt.automata.regex.element.Grouping
import gt.automata.regex.element.Operator
import gt.automata.regex.postfix.RegexToPostfix
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ThompsonTransformTest {

    private val regexToNFA = ThomptsonTransformConfig.getThompsonTransform()

    @Test
    fun `should generate a nfa from a regex expression`() {
        val regex = RegexToPostfix(
            listOf(
                Character("a"),
                Operator.Concatenation,
                Grouping.OpenParenthesis,
                Character("a"),
                Operator.Or,
                Character("b"),
                Grouping.CloseParenthesis,
                Operator.Closure,
                Operator.Concatenation,
                Character("b")
            )
        )

        val expectedNfa = nfa<Int, String> {
            states {
                for(i in 1..10) state { i }
            }
            initialState(1)
            finalStates(10)
            transitions {
                1 to 2 by "a"
                2 to 3 by epsilon
                2 to 9 by epsilon
                3 to 4 by epsilon
                3 to 6 by epsilon
                4 to 5 by "a"
                6 to 7 by "b"
                5 to 8 by epsilon
                7 to 8 by epsilon
                8 to 3 by epsilon
                8 to 9 by epsilon
                9 to 10 by "b"
            }
        }

        val generatedNfa = regexToNFA(regex)

        Assertions.assertEquals(expectedNfa.states.size, generatedNfa.states.size)
        Assertions.assertEquals(expectedNfa.transitionTable.size, generatedNfa.transitionTable.size)

    }


}
