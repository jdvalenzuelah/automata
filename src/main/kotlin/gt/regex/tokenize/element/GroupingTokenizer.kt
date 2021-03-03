package gt.regex.tokenize.element

import gt.regex.element.Grouping

internal object GroupingTokenizer : TokenizeRegexElement<Grouping> {

    override fun invoke(str: String): Grouping? {
        return Grouping::class.sealedSubclasses
            .firstOrNull { it.objectInstance?.id == str }
            ?.objectInstance
    }

}
