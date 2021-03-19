package org.github.compiler.regularExpressions.regex.transform

import org.github.compiler.regularExpressions.regex.RegularExpression

interface RegexTransforms<T : Any> : (RegularExpression) -> T
