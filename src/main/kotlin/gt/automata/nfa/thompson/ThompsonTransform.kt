package gt.automata.nfa.thompson

import gt.automata.nfa.RegexToNFA
import gt.automata.nfa.NonDeterministicFiniteAutomata
import gt.automata.regex.RegexExpression
import gt.automata.regex.element.Character
import gt.automata.regex.element.Grouping
import gt.automata.regex.element.Operator
import gt.automata.regex.postfix.InfixToPostfix

class ThompsonTransform(
    private val regexToPostFix: InfixToPostfix,
    private val thomptsonRules: ThomptsonRules
) : RegexToNFA<Int, String>  {


    override fun invoke(regex: RegexExpression): NonDeterministicFiniteAutomata<Int, String> {
        val postfixExpression = regexToPostFix(regex)
        val operationStack = ArrayDeque<NonDeterministicFiniteAutomata<Int, String>>()

        postfixExpression.forEach { el ->
            when(el) {
                is Grouping -> throw error("Invalid postfix expression was generated from input expression $regex")
                is Character -> operationStack.addLast(thomptsonRules.symbol(el.char))
                Operator.Concatenation -> {
                    val nfa2 = operationStack.removeLast()
                    val nfa1 = operationStack.removeLast()
                    operationStack.addLast(thomptsonRules.concat(nfa1, nfa2))
                }
                Operator.Closure -> operationStack.addLast(thomptsonRules.closure(operationStack.removeLast()))
                Operator.Or -> {
                    val nfa2 = operationStack.removeLast()
                    val nfa1 = operationStack.removeLast()
                    operationStack.addLast(thomptsonRules.or(nfa1, nfa2))
                }
            }
        }
        return operationStack.removeLast()
    }

}
