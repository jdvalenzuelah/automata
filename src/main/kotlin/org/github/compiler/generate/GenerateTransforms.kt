package org.github.compiler.generate

import org.github.compiler.atg.ATG
import org.github.compiler.atg.Identifiable
import org.github.compiler.regularExpressions.transforms.Transform
import org.github.compiler.regularExpressions.transforms.then

val getSpecName = Transform<ATG, String> { "${it.compilerName}Spec" }

val getEnumName = getSpecName.then { "${it}TokenType" }

val getEnumConstantName = Transform<Identifiable, String> {
    it.name.toUpperCase()
}

