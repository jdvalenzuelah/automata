package gt.automata.nfa.models

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class NFABuilderTest {

    @Test
    fun `should build a nfa from dsl`() {
        val expectedNfa = NFA(
            states = listOf(State("A"), State("B"), State("C")),
            initialState = State("A"),
            finalStates = listOf(State("C")),
            transitionTable = mapOf(
                State("A") to mapOf("1" to listOf(State("B"), State("A"))),
                State("B") to mapOf("2" to listOf(State("C")))
            )
        )

        val dslNfa = nfa<String, String> {
            states {
                +"A"
                +"B"
                +"C"
            }

            initialState("A")

            finalStates("C")

            transitions {
                "A" to "B" by "1"
                "A" to "A" by "1"
                "B" to "C" by "2"
            }
        }

        Assertions.assertEquals(expectedNfa, dslNfa)
    }

}
