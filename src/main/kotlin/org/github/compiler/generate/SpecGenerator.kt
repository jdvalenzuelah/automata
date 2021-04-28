package org.github.compiler.generate

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.buildCodeBlock
import org.github.compiler.atg.ATG
import org.github.compiler.regularExpressions.regexImpl.StatefulRegex
import org.github.compiler.regularExpressions.transforms.Transform

object SpecGenerator : Transform<ATG, FileSpec> {


    override fun invoke(atg: ATG): FileSpec {
        val tokenTypeEnum = TypeSpec.enumBuilder("TokenType")
            .apply {
                atg.keywords.forEach {
                    addEnumConstant(it.name.toUpperCase())
                }

                atg.tokens.forEach {
                    addEnumConstant(it.name.toUpperCase())
                }
            }
            .build()

        val charactersObject = TypeSpec.objectBuilder("Characters")
            .apply {
                atg.characters.forEach { character ->
                    addProperty(
                        PropertySpec.builder(character.name, String::class)
                            .mutable(false)
                            .initializer("%S", character.def)
                            .build()
                    )
                }

                addProperty(
                    PropertySpec.builder("IGNORE_SET", String::class)
                        .mutable(false)
                        .initializer("%S", atg.ignoreSet.def)
                        .build()
                )
            }
            .build()

        val patternsObject = TypeSpec.objectBuilder("Patterns")
            .apply {

                atg.tokens.forEach {
                    addProperty(
                        PropertySpec.builder(it.name, StatefulRegex::class)
                            .initializer("%S.toStatefulRegex()", it.regex)
                            .build()
                    )
                }

                atg.keywords.forEach {
                    addProperty(
                        PropertySpec.builder(it.name, StatefulRegex::class)
                            .initializer("%S.toStatefulRegex()", it.def)
                            .build()
                    )
                }

                atg.characters.forEach { character ->
                    addProperty(
                        PropertySpec.builder(character.name, StatefulRegex::class)
                            .mutable(false)
                            .initializer("%S.toStatefulRegex()", character.asRegexExpression())
                            .build()
                    )
                }
            }
            .build()

        val specName = "${atg.compilerName}Spec"
        val spec = TypeSpec.objectBuilder(specName)
            .addType(charactersObject)
            .addType(tokenTypeEnum)
            .addType(patternsObject)
            .build()

        return FileSpec.builder("org.github.compiler.generated", specName)
            .addImport("org.github.compiler.regularExpressions.regexImpl", "toStatefulRegex")
            .addType(spec)
            .build()
    }

}
