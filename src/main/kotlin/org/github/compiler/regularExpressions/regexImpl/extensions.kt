package org.github.compiler.regularExpressions.regexImpl

import org.github.compiler.regularExpressions.Regex
import org.github.compiler.regularExpressions.transforms.Transform

private val stringToRegex = Transform<String, Regex> { RegexImpl(it) }

fun String.toRegex(): Regex = stringToRegex(this)

fun String.matches(regex: Regex): Boolean = regex.matches(this)
