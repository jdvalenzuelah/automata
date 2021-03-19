package org.github.compiler.regularExpressions.transforms.regex

import org.github.compiler.regularExpressions.regex.RegularExpression
import org.github.compiler.regularExpressions.regex.elements.RegexElement
import org.github.compiler.regularExpressions.regex.transform.RegexTransforms
import org.github.compiler.regularExpressions.syntaxTree.INode
import org.github.compiler.regularExpressions.syntaxTree.models.RegexSyntaxTree
import org.tinylog.kotlin.Logger

class RegexToSyntaxTree(
    private val regexToTree: RegexTransforms<INode<RegexElement>>
) : RegexTransforms<RegexSyntaxTree> {

    override fun invoke(p1: RegularExpression): RegexSyntaxTree {
        Logger.info("Generating regex syntax tree from regex $p1")
        return RegexSyntaxTree(regexToTree(p1), p1)
    }

}
