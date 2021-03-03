package gt.regex.tokenize.element

import gt.regex.element.Character

internal object CharacterTokenizer : TokenizeRegexElement<Character> {
    override fun invoke(str: String): Character = Character(str)
}
