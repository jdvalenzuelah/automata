package org.github.compiler.regularExpressions

interface Regex {
    companion object
    fun matches(str: CharSequence): Boolean
}
