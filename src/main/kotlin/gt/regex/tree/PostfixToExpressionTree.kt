package gt.regex.tree

import gt.regex.RegexExpression
import gt.regex.element.RegexElement
import gt.tree.INode

fun interface PostfixToExpressionTree : (RegexExpression) -> INode<RegexElement>
