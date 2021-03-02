package gt.automata.nfa.models

import gt.automata.models.State
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class NFATest {

    @Test
    fun `move function should return expected state after input`() {

        val nfa = nfa<String, String> {
            states {
                +"A"
                +"B"
                +"C"
            }
            initialState("A")
            finalStates("B")
            transitions {
                "A" to "B" by "X"
                "A" to "C" by "Z"
                "B" to "C" by "Y"
            }
        }

        println(nfa)

        Assertions.assertEquals(listOf(State("B")), nfa.move(State("A"), "X"))
        Assertions.assertEquals(listOf(State("C")), nfa.move(State("B"), "Y"))
        Assertions.assertEquals(listOf(State("C")), nfa.move(State("A"), "Z"))


    }

}
