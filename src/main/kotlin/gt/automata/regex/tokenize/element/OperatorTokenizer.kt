package gt.automata.regex.tokenize.element

import gt.automata.regex.element.Operator

internal object OperatorTokenizer : TokenizeRegexElement<Operator> {

    override fun invoke(str: String): Operator? {
        return Operator::class.sealedSubclasses
            .firstOrNull { it.objectInstance?.id == str }
            ?.objectInstance
    }

}
