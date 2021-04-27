package org.github.compiler.regularExpressions.regexImpl

import org.github.compiler.regularExpressions.automata.IState
import org.github.compiler.regularExpressions.automata.dfa.DeterministicFiniteAutomata
import org.github.compiler.regularExpressions.transforms.regex.ParseRegexToAutomataFactory
import org.github.compiler.regularExpressions.Regex
import org.github.compiler.regularExpressions.transforms.Transform

class StatefulRegex<S, A: DeterministicFiniteAutomata<S, Char>>(
    private val regexAutomata: A,
): Regex {

    companion object {
        operator fun invoke(pattern: String): StatefulRegex<*, *> {
            val regex = ParseRegexToAutomataFactory.dfaParser()
                .fromRegex()
                .invoke(pattern)

            return StatefulRegex(regex)
        }
    }

    private var lastState: IState<S>? = regexAutomata.initialState

    fun hasNext(char: Char): Boolean = lastState != null && regexAutomata.move(lastState!!, char) != null

    fun move(char: Char)  {
        if(lastState != null)
            lastState = regexAutomata.move(lastState!!, char)
    }

    fun isAccepted(): Boolean = lastState != null && lastState in regexAutomata.finalStates

    fun reset() {
        lastState = regexAutomata.initialState
    }

    override fun matches(str: CharSequence): Boolean {
        str.forEach { move(it) }
        val accepted = isAccepted()
        reset()
        return accepted
    }

}

private val stateFulRegex = Transform<String, StatefulRegex<*, *>> { StatefulRegex(it) }

fun String.toStatefulRegex() = stateFulRegex(this)
