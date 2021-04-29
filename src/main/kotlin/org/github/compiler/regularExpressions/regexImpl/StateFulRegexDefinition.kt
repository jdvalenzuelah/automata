package org.github.compiler.regularExpressions.regexImpl

interface IRegexDefinition<T> : StatefulRegex {
    fun getResult(): T?
}

class StateFulRegexDefinition<T>(
    private val definitions: Collection<Pair<T, StatefulRegex>>
): IRegexDefinition<T> {

    constructor(definitions: Map<T, StatefulRegex>) : this(definitions.toList())

    override fun hasNext(char: Char): Boolean = definitions.any { it.second.hasNext(char) }

    override fun isAccepted(): Boolean = definitions.any { it.second.isAccepted() }

    override fun move(char: Char) {
        definitions.forEach { it.second.move(char) }
    }

    override fun reset() {
        definitions.forEach { it.second.reset() }
    }

    override fun matches(str: CharSequence): Boolean {
        str.forEach { move(it) }
        val accepted = isAccepted()
        reset()
        return accepted
    }

    override fun getResult(): T? = definitions.firstOrNull { it.second.isAccepted() }?.first

}
