package gt.automata.dfa

import gt.automata.dfa.models.dfa
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class DFASimulationTest {

    private val sut = DFASimulation<String, String>()

    @Test
    fun `should return true if input is accepted`() {
        val dfa = dfa<String, String> { // (a|b)*abb
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
        Assertions.assertTrue(sut.simulate(dfa, "ababb".map { it.toString() }))
    }

    @Test
    fun `should return false if input is not accepted`() {
        val dfa = dfa<String, String> { /* (a|b)*ab */
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
        Assertions.assertFalse(sut.simulate(dfa, "ababba".map { it.toString() }))
    }

    @Test
    fun `should return false if input is not valid accepted`() {
        val dfa = dfa<String, String> { /* (a|b)*ab */
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
        Assertions.assertFalse(sut.simulate(dfa, "abcd".map { it.toString() }))
    }

}
