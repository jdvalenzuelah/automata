package org.github.compiler.regularExpressions.transforms.regex


import org.github.compiler.regularExpressions.regex.RegularExpression
import org.github.compiler.regularExpressions.regex.elements.RegexElement
import org.github.compiler.regularExpressions.regex.elements.Grouping
import org.github.compiler.regularExpressions.regex.elements.Character
import org.github.compiler.regularExpressions.regex.elements.Operator
import org.github.compiler.regularExpressions.regex.elements.Augmented
import org.github.compiler.regularExpressions.regex.transform.RegexTransforms
import org.github.compiler.regularExpressions.syntaxTree.INode
import org.github.compiler.regularExpressions.syntaxTree.models.Node
import org.tinylog.kotlin.Logger

object RegexToTree : RegexTransforms<INode<RegexElement>> {

    override fun invoke(p1: RegularExpression): INode<RegexElement> {
        Logger.info("Generating regex tree for regex=$p1")
        val stack = ArrayDeque<Node<RegexElement>>()

        var position = 1
        p1.forEach { el ->
            when(el) {
                is Grouping -> error("Invalid postfix expression=$p1!")
                is Character -> {
                    val tmpPosition: Int? = if (el.isEpsilon()) null else position
                    stack.addLast(Node(el, null, null, position)).also { if(tmpPosition != null) position++ }
                }
                is Augmented -> stack.addLast(Node(el, null, null, position)).also { position++ }
                is Operator.PositiveClosure -> {
                    //rr*
                    val operand = stack.removeLast()
                    stack.addLast(Node(Operator.Concatenation, operand, Node(Operator.Closure, operand.copy(), null)))
                }
                is Operator.ZeroOrOne -> {
                    //r|epsilon
                    val operand = stack.removeLast()
                    stack.addLast(Node(Operator.Or, operand, Node(Character.EPSILON, null, null)))
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
