package org.github.compiler.regularExpressions.regex.transform.augment

import org.github.compiler.regularExpressions.regex.RegularExpression
import org.github.compiler.regularExpressions.regex.elements.Augmented
import org.github.compiler.regularExpressions.regex.elements.Grouping
import org.github.compiler.regularExpressions.regex.elements.Operator
import org.github.compiler.regularExpressions.regex.elements.RegexElement
import org.github.compiler.regularExpressions.regex.transform.RegexTransforms

object AugmentRegex : RegexTransforms<RegularExpression> {
    override fun invoke(regex: RegularExpression): Collection<RegexElement> {
        return listOf(Grouping.OpenParenthesis) + regex + listOf(Grouping.CloseParenthesis, Operator.Concatenation, Augmented.EndMarker)
    }
}
