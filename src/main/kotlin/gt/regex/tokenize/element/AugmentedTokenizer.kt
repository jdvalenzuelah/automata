package gt.regex.tokenize.element

import gt.regex.element.Augmented

object AugmentedTokenizer : TokenizeRegexElement<Augmented> {

    override fun invoke(p1: String): Augmented? {
        return Augmented::class.sealedSubclasses
            .firstOrNull { it.objectInstance?.id == p1 }
            ?.objectInstance
    }

}
