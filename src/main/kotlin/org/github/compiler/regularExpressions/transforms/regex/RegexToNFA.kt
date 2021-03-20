package org.github.compiler.regularExpressions.transforms.regex

import org.github.compiler.regularExpressions.automata.nfa.NonDeterministicFiniteAutomata
import org.github.compiler.regularExpressions.automata.nfa.operations.ThompsonConstruction
import org.github.compiler.regularExpressions.regex.RegularExpression
import org.github.compiler.regularExpressions.regex.elements.Grouping
import org.github.compiler.regularExpressions.regex.elements.Character
import org.github.compiler.regularExpressions.regex.elements.Operator
import org.github.compiler.regularExpressions.regex.transform.RegexTransforms

class RegexToNFA<S>(
    private val thompsonConstruction: ThompsonConstruction<S, Char>,
) : RegexTransforms<NonDeterministicFiniteAutomata<S, Char>> {

    override fun invoke(postfixExpression: RegularExpression): NonDeterministicFiniteAutomata<S, Char> {
        val operationStack = ArrayDeque<NonDeterministicFiniteAutomata<S, Char>>()

        postfixExpression.forEach { el ->
            when(el) {
                is Grouping -> throw error("Invalid postfix expression was generated from input expression $postfixExpression")
                is Character -> operationStack.addLast(thompsonConstruction.symbol(el.char))
                Operator.Concatenation -> {
                    val nfa2 = operationStack.removeLast()
                    val nfa1 = operationStack.removeLast()
                    operationStack.addLast(thompsonConstruction.concat(nfa1, nfa2))
                }
                Operator.Closure -> operationStack.addLast(thompsonConstruction.closure(operationStack.removeLast()))
                Operator.PositiveClosure -> operationStack.addLast(thompsonConstruction.positiveClosure(operationStack.removeLast()))
                Operator.ZeroOrOne ->operationStack.addLast(thompsonConstruction.zeroOrOne(operationStack.removeLast()))
                Operator.Or -> {
                    val nfa2 = operationStack.removeLast()
                    val nfa1 = operationStack.removeLast()
                    operationStack.addLast(thompsonConstruction.or(nfa1, nfa2))
                }
            }
        }
        return operationStack.removeLast()
    }

}
