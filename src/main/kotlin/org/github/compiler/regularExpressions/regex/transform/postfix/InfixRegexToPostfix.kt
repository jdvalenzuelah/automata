package org.github.compiler.regularExpressions.regex.transform.postfix

import org.github.compiler.regularExpressions.regex.elements.*
import org.github.compiler.regularExpressions.regex.RegularExpression
import org.github.compiler.regularExpressions.regex.transform.RegexTransforms
import org.tinylog.kotlin.Logger

object InfixRegexToPostfix : RegexTransforms<RegularExpression> {

    private fun RegexElement.precedence(): Int {
        return when(this@precedence) {
            is Operator -> this.precedence
            is Grouping -> Int.MIN_VALUE
            is Character, is Augmented -> Int.MAX_VALUE
        }
    }

    override fun invoke(infix: RegularExpression): RegularExpression {
        Logger.info("Converting infixRegex=$infix to postfix")
        val inputQueue = ArrayDeque<RegexElement>().apply { infix.toCollection(this@apply) }
        val outputQueue = ArrayDeque<RegexElement>()

        val operatorStack = ArrayDeque<RegexElement>()

        while(inputQueue.isNotEmpty()) {
            when(val el = inputQueue.removeFirst()) {
                is Character, is Augmented -> outputQueue.addLast(el)
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
            .also { Logger.info("Generated postfix=$it regex from infix=$infix") }
    }

}
