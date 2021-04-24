package org.github.compiler.regularExpressions

interface Regex {
    fun matches(str: CharSequence): Boolean
}
