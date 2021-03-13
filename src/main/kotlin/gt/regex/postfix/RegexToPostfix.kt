package gt.regex.postfix

import gt.regex.RegexExpression
import gt.regex.element.Character
import gt.regex.element.Grouping
import gt.regex.element.Operator
import gt.regex.element.RegexElement
import java.lang.IllegalStateException
import kotlin.jvm.Throws

object RegexToPostfix : InfixToPostfix {

    private fun RegexElement.precedence(): Int {
        return when(this@precedence) {
            is Operator -> this.precedence
            is Grouping -> Int.MIN_VALUE
            is Character -> Int.MAX_VALUE
            else -> Int.MAX_VALUE
        }
    }

    @Throws(IllegalStateException::class)
    override fun invoke(infix: RegexExpression): RegexExpression {
        val inputQueue = ArrayDeque<RegexElement>().apply { infix.toCollection(this@apply) }
        val outputQueue = ArrayDeque<RegexElement>()

        val operatorStack = ArrayDeque<RegexElement>()

        while(inputQueue.isNotEmpty()) {
            when(val el = inputQueue.removeFirst()) {
                is Character -> outputQueue.addLast(el)
                is Operator -> {
                    if(operatorStack.isEmpty()) {
                        operatorStack.addLast(el)
                    } else {
                        while(operatorStack.isNotEmpty() && operatorStack.last().precedence() >= el.precedence()) {
                            outputQueue.addLast(operatorStack.removeLast())
                        }
                        operatorStack.add(el)
                    }
                }
                Grouping.OpenParenthesis -> operatorStack.addLast(el)
                Grouping.CloseParenthesis -> {
                    while(operatorStack.isNotEmpty() && operatorStack.last() != Grouping.OpenParenthesis) {
                        outputQueue.addLast(operatorStack.removeLast())
                    }
                    val last = operatorStack.removeLastOrNull()
                    if(last == null || last != Grouping.OpenParenthesis)
                        error("Incorrect parenthesis on input infix expression $infix")
                }
            }
        }

        while(operatorStack.isNotEmpty()) outputQueue.addLast(operatorStack.removeLast())

        return outputQueue.toList()
    }

}
