package org.github.compiler.regularExpressions.transforms

fun interface Transform<in I, out O>: (I) -> O

fun <A, B, D> Transform<A, B>.then(next: Transform<B, D> ) = Transform<A, D> { next(this(it)) }
