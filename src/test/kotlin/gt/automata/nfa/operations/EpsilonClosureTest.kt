package gt.automata.nfa.operations

import gt.automata.models.State
import gt.automata.nfa.epsilon
import gt.automata.nfa.models.nfa
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class EpsilonClosureTest {

    private val sut = EpsilonClosure<String, String>(epsilon)

    private val testNfa = nfa<String, String> {
        states { for(i in 0..10) state { "$i" } }
        initialState("0")
        finalStates("10")
        transitions {
            "0" to "1" by epsilon
            "0" to "7" by epsilon
            "1" to "2" by epsilon
            "1" to "4" by epsilon
            "2" to "3" by "a"
            "3" to "6" by epsilon
            "4" to "5" by "b"
            "5" to "6" by epsilon
            "6" to "1" by epsilon
            "6" to "7" by epsilon
            "7" to "8" by "a"
            "8" to "9" by "b"
            "9" to "10" by "b"
        }
    }

    @Test
    fun `should calculate epsilon closure for a single state`() {
        val expected = listOf(
            State("0"),
            State("1"),
            State("2"),
            State("4"),
            State("7")
        )

        val epsilonClosure = sut.eClosure(testNfa, State("0"))

        Assertions.assertEquals(expected.size, epsilonClosure.size)
        Assertions.assertTrue(expected.containsAll(epsilonClosure))
    }

    @Test
    fun `should calculate epsilon closure for more than one state`() {
        val expected = listOf(
            State("1"),
            State("2"),
            State("3"),
            State("4"),
            State("6"),
            State("7"),
            State("8")
        )

        val epsilonClosure = sut.eClosure(testNfa, State("3"), State("8"))

        Assertions.assertEquals(expected.size, epsilonClosure.size)
        Assertions.assertTrue(expected.containsAll(epsilonClosure))
    }



}
