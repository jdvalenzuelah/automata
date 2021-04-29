package org.github.compiler.generate

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import org.github.compiler.atg.ATG
import org.github.compiler.atg.specification.Spec
import org.github.compiler.atg.specification.TokenType
import org.github.compiler.regularExpressions.regexImpl.StatefulRegex
import org.github.compiler.regularExpressions.transforms.Transform

//TODO: Fix generated regexss
object SpecGenerator : Transform<ATG, FileSpec> {


    override fun invoke(atg: ATG): FileSpec {

        val specName = "${atg.compilerName}Spec"

        val enumName = "${specName}TokenType"
        val tokenTypeEnum = TypeSpec.enumBuilder(enumName)
            .addSuperinterface(TokenType::class)
            .apply {
                atg.keywords.forEach { addEnumConstant(it.name.toUpperCase()) }
                atg.tokens.forEach { addEnumConstant(it.name.toUpperCase()) }
                atg.characters.forEach { addEnumConstant(it.name.toUpperCase()) }
            }
            .build()

        val patternsMap = PropertySpec
            .builder("patternsMap", Map::class.asClassName().parameterizedBy(TokenType::class.asTypeName(), StatefulRegex::class.asTypeName()))
            .addModifiers(KModifier.PRIVATE)
            .initializer(
                CodeBlock.builder()
                    .apply {
                        addStatement("mapOf(")
                        atg.keywords.forEach { addStatement("\t$enumName.${it.name.toUpperCase()} to %S.toStatefulRegex(),", it.def) }
                        atg.tokens.forEach { addStatement("\t$enumName.${it.name.toUpperCase()} to %S.toStatefulRegex(),", it.regex) }
                        atg.characters.forEach { addStatement("\t$enumName.${it.name.toUpperCase()} to %S.toStatefulRegex(),", it.asRegexExpression()) }
                        addStatement(")")
                    }
                    .build()
            )
            .build()

        val keywordsMap = PropertySpec
            .builder("keywordsMap", Map::class.asClassName().parameterizedBy(String::class.asTypeName(), TokenType::class.asTypeName()))
            .addModifiers(KModifier.PRIVATE)
            .initializer(
                CodeBlock.builder()
                    .apply {
                        addStatement("mapOf(")
                        atg.keywords.forEach { addStatement("\t%S to ${enumName}.${it.name.toUpperCase()},", it.def) }
                        addStatement(")")
                    }
                    .build()
            )
            .build()

        val ignoreSet = PropertySpec.builder("ignoreChars", Collection::class.asClassName().parameterizedBy(Char::class.asTypeName()))
            .addModifiers(KModifier.PRIVATE)
            .initializer(
                CodeBlock.builder()
                    .apply {
                        addStatement("%S.toList()", atg.ignoreSet.def)
                    }
                    .build()
            )
            .build()

        val allPatterns = FunSpec.builder("getAllPatterns")
            .returns(Map::class.asClassName().parameterizedBy(TokenType::class.asTypeName(), StatefulRegex::class.asTypeName()))
            .addModifiers(KModifier.OVERRIDE)
            .addStatement("return patternsMap")
            .build()

        val ignore = FunSpec.builder("ignoreSet")
            .returns(Collection::class.asClassName().parameterizedBy(Char::class.asTypeName()))
            .addModifiers(KModifier.OVERRIDE)
            .addStatement("return ignoreChars")
            .build()

        val getKeyword = FunSpec.builder("getAllKeywords")
            .returns(Map::class.asClassName().parameterizedBy(String::class.asTypeName(), TokenType::class.asTypeName()))
            .addModifiers(KModifier.OVERRIDE)
            .addStatement("return keywordsMap")
            .build()

        val spec = TypeSpec.objectBuilder(specName)
            .addSuperinterface(Spec::class)
            .addType(tokenTypeEnum)
            .addProperty(patternsMap)
            .addProperty(keywordsMap)
            .addProperty(ignoreSet)
            .addFunction(allPatterns)
            .addFunction(getKeyword)
            .addFunction(ignore)
            .build()

        return FileSpec.builder("org.github.compiler.generated", specName)
            .addImport("org.github.compiler.regularExpressions.regexImpl", "toStatefulRegex")
            .addType(spec)
            .build()
    }

}