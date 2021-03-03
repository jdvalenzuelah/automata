package gt.automata.nfa

import gt.automata.nfa.models.nfa
import gt.automata.nfa.operations.EpsilonClosure
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class NFASimulationTest {

    private val sut = NFASimulation<String, String>(EpsilonClosure(epsilon))

    private val testNfa = nfa<String, String> {
        states { for(i in 0..2) state { "$i" } }
        initialState("0")
        finalStates("2")
        transitions {
            "0" to "1" by "a"
            "0" to "2" by epsilon
            "1" to "1" by "a"
            "1" to "1" by "b"
            "1" to "2" by "b"
        }
    }

    @Test
    fun `should return true if input is accepted by nfa`() {
        val input = "abab".map { it.toString() }

        Assertions.assertTrue(sut.simulate(testNfa, input))

    }

    @Test
    fun `should return false if input is accepted by nfa`() {
        val input = "abb".map { it.toString() }

        Assertions.assertTrue(sut.simulate(testNfa, input))

    }


}
