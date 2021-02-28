package gt.automata.nfa.thompson

import gt.automata.nfa.epsilon
import gt.automata.nfa.models.nfa
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ThompsonConstructionTest {

    private val sut = Thomptson()

    @Test
    fun `should build a nfa from a symbol`() {
        val symbol = "a"
        val expectedNfa = nfa<Int, String> {
            states {
                state { 1 }
                state { 2 }
            }
            initialState(1)
            finalStates(2)
            transitions {
                1 to 2 by symbol
            }
        }

        val construct = sut.symbol(symbol)

        Assertions.assertEquals(expectedNfa, construct)

    }

    @Test
    fun `should build a nfa from epsion`() {
        val symbol = epsilon
        val expectedNfa = nfa<Int, String> {
            states {
                state { 1 }
                state { 2 }
            }
            initialState(1)
            finalStates(2)
            transitions {
                1 to 2 by symbol
            }
        }

        val construct = sut.empty()

        Assertions.assertEquals(expectedNfa, construct)
    }

    @Test
    fun `should build nfa from or of 2 nfas`() {
        val symbol1 = "a"
        val nfa1 = nfa<Int, String> {
            states {
                state { 6 }
                state { 5 }
            }
            initialState(6)
            finalStates(5)
            transitions {
                6 to 5 by symbol1
            }
        }

        val symbol2 = "b"
        val nfa2 = nfa<Int, String> {
            states {
                state { 4 }
                state { 3 }
            }
            initialState(4)
            finalStates(3)
            transitions {
                4 to 3 by symbol2
            }
        }

        val expectedNfa = nfa<Int, String> {
            states {
                state { 6 }
                state { 5 }
                state { 4 }
                state { 3 }
                state { 1 }
                state { 2 }
            }
            initialState(1)
            finalStates(2)

            transitions {
                6 to 5 by symbol1
                4 to 3 by symbol2
                1 to 6 by epsilon
                1 to 4 by epsilon
                5 to 2 by epsilon
                3 to 2 by epsilon
            }
        }

        val construct = sut.or(nfa1, nfa2)


        Assertions.assertEquals(expectedNfa, construct)
    }

    @Test
    fun `should build an nfa from a closure of an nfa`() {
        val symbol1 = "a"
        val nfa1 = nfa<Int, String> {
            states {
                state { 6 }
                state { 5 }
            }
            initialState(6)
            finalStates(5)
            transitions {
                6 to 5 by symbol1
            }
        }

        val expectedNfa = nfa<Int, String> {
            states {
                state { 1 }
                state { 2 }
                state { 6 }
                state { 5 }
            }
            initialState(1)
            finalStates(2)
            transitions {
                6 to 5 by symbol1
                1 to 6 by epsilon
                1 to 2 by epsilon
                5 to 6 by epsilon
                5 to 2 by epsilon
            }
        }

        val construct = sut.closure(nfa1)
        println(construct)

        Assertions.assertEquals(expectedNfa, construct)

    }

}
