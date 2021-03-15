package gt.regex.tree

import gt.automata.nfa.epsilon
import gt.regex.RegexExpression
import gt.regex.element.*
import gt.tree.models.Node

object RegexToTree : PostfixToExpressionTree {

    override fun invoke(p1: RegexExpression): Node<RegexElement> {
        val stack = ArrayDeque<Node<RegexElement>>()

        var position = 1
        p1.forEach { el ->
            when(el) {
                is Grouping -> error("Invalid postfix expression=$p1!")
                is Character, is Augmented -> stack.addLast(Node(el, null, null, position)).also { position++ }
                is Operator.PositiveClosure -> {
                    //rr*
                    val operand = stack.removeLast()
                    stack.addLast(Node(Operator.Concatenation, operand, Node(Operator.Closure, operand.copy(), null)))
                }
                is Operator.ZeroOrOne -> {
                    //r|epsilon
                    val operand = stack.removeLast()
                    stack.addLast(Node(Operator.Or, operand, Node(Character(epsilon), null, null)))
                }
                is Operator -> {
                    if(el.isUnary) {
                        val operand = stack.removeLast()
                        stack.addLast(Node(el, operand, null))
                    } else {
                        val operand2 = stack.removeLast()
                        val operand1 = stack.removeLast()
                        stack.addLast(Node(el, operand1, operand2))
                    }
                }
            }
        }

        return stack.removeLast()
    }

}
