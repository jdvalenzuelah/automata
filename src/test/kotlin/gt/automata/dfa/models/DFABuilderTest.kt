package gt.automata.dfa.models

import gt.automata.models.State
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions

class DFABuilderTest {

    @Test
    fun `should build dfa from dsl`() {

        val expectedDfa = DFA<String, String>(
            initialState = State("0"),
            finalStates = listOf(State("3")),
            states = listOf(State("0"), State("1"), State("2"), State("3")),
            transitionTable = mapOf(
                State("0") to mapOf( "b" to State("0"), "a" to State("1") ),
                State("1") to mapOf( "a" to State("1"), "b" to State("2") ),
                State("2") to mapOf( "a" to State("1"), "b" to State("3") ),
                State("3") to mapOf( "a" to State("1"), "b" to State("0") )
            ),
            alphabet = setOf("a", "b")
        )

        val dfa = dfa<String, String> {
            states {
                +"0"
                +"1"
                +"2"
                +"3"
            }

            initialState("0")
            finalStates("3")

            transitions {
                "0" to "0" by "b"
                "0" to "1" by "a"
                "1" to "1" by "a"
                "1" to "2" by "b"
                "2" to "1" by "a"
                "2" to "3" by "b"
                "3" to "0" by "b"
                "3" to "1" by "a"
            }
        }

        Assertions.assertEquals(expectedDfa, dfa)

    }

}
