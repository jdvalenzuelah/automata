package gt.automata.dfa.models

import gt.automata.models.State
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class DFATest {

    @Test
    fun `move function should return expected state after input`() {
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
                "1" to "3" by "c"
            }
        }

        Assertions.assertEquals(State("0"), dfa.move(State("0"), "b"))
        Assertions.assertEquals(State("1"), dfa.move(State("0"), "a"))
        Assertions.assertEquals(State("1"), dfa.move(State("1"), "a"))
        Assertions.assertEquals(State("2"), dfa.move(State("1"), "b"))
        Assertions.assertEquals(State("3"), dfa.move(State("1"), "c"))
    }

}
