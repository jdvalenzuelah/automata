package org.github.compiler.regularExpressions.regex.tokenize.element

import org.github.compiler.regularExpressions.regex.elements.*
import kotlin.reflect.KClass

private object Tokenizer {
    fun <K : RegexElement> getTokenAsOrNull(to: KClass<K>, id: Char): K? {
        return to.sealedSubclasses
            .firstOrNull { it.objectInstance?.id == id }
            ?.objectInstance
    }
}

internal object TokenizeCharacter : RegexElementTokenizer<Character> {
    override fun invoke(id: Char): Character = Character(id)
}

internal object TokenizeOperator : RegexElementTokenizer<Operator> {
    override fun invoke(id: Char): Operator? = Tokenizer.getTokenAsOrNull(Operator::class, id)
}

internal object TokenizeGrouping : RegexElementTokenizer<Grouping> {
    override fun invoke(id: Char): Grouping? = Tokenizer.getTokenAsOrNull(Grouping::class, id)
}

internal object TokenizeAugmented : RegexElementTokenizer<Augmented> {
    override fun invoke(id: Char): Augmented? = Tokenizer.getTokenAsOrNull(Augmented::class, id)
}
