package gt.automata.regex.tokenize.element

import gt.automata.regex.element.Character

internal object CharacterTokenizer : TokenizeRegexElement<Character> {
    override fun invoke(str: String): Character = Character(str)
}
