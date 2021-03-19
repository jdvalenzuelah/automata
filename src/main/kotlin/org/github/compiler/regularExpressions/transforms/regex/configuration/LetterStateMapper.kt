package org.github.compiler.regularExpressions.transforms.regex.configuration

import org.github.compiler.regularExpressions.automata.nfa.operations.IdGenStrategy

object LetterStateMapper : IdGenStrategy<String> {

    private val letters = 'A'..'Z'
    private var iterator = letters.iterator()
    private var completeItersCounter = 1

    override fun invoke(): String {
        return if(iterator.hasNext())
            iterator.next().toString().repeat(completeItersCounter)
        else {
            iterator = letters.iterator()
            completeItersCounter++
            iterator.next().toString().repeat(completeItersCounter)
        }
    }
}
