package org.github.compiler.generate

import kotlin.reflect.KClass
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import org.github.compiler.atg.ATG
import org.github.compiler.atg.Character
import org.github.compiler.atg.scanner.streams.Stream
import org.github.compiler.atg.specification.Spec
import org.github.compiler.atg.specification.TokenRef
import org.github.compiler.atg.specification.TokenType
import org.github.compiler.regularExpressions.regexImpl.StatefulRegex
import org.github.compiler.regularExpressions.transforms.Transform

object SpecGenerator : Transform<ATG, FileSpec> {

    private fun CodeBlock.Builder.encodeMap(k1: String, k2: Character) {
        val regex = k2.asRegexExpression()

        if(regex.any { it.toInt() < 21 }) {
            val list = regex.toList().joinToString(separator = ",") { "${it.toInt()}" }
            val code = "listOf($list).joinToString(separator = \"\"){ it.toChar().toString() }.toStatefulRegex()"
            addStatement("$k1 to $code,")
        } else {
            addStatement("$k1 to %S.toStatefulRegex(),", regex)
        }


    }

    private fun getInitCode(atg: ATG): CodeBlock {
        val initCode = CodeBlock.builder()
        atg.code.forEach { codeBlock ->
            if(!codeBlock.maybeIsType()) {
                initCode.addStatement(codeBlock)
            }
        }
        return initCode.build()
    }

    private fun TypeSpec.Builder.inferCustomTypes(atg: ATG): TypeSpec.Builder = apply {
        atg.code.forEach { codeBlock ->
            if(codeBlock.maybeIsType()) {
                addType(codeBlock.buildType())
            }
        }
    }

    private fun String.maybeIsType(): Boolean = contains("(.*)class\\s(.*)".toRegex())

    private fun String.buildType(): TypeSpec {
        val modifiers = mutableListOf<String>()
        val params = mutableListOf<PropertySpec>()

        val constructor = FunSpec.constructorBuilder()

        var name = ""

        val tokens = ArrayDeque<String>()
        tokens.addAll(split(" "))

        while(tokens.isNotEmpty()) {
            val current = tokens.removeFirst()
            when {
                current == "class" -> { // next should be name
                    check(tokens.first().isValidName())
                    name = tokens.removeFirst()
                }
                current.isModifier() ->  modifiers.add(current)
                current == "val" || current == "var" -> {
                    val isMutable = current == "var"
                    val propName = tokens.removeFirst()
                    check(tokens.first() == ":")
                    tokens.removeFirst() //ignore :
                    val propType = tokens.removeFirst()
                    constructor.addParameter(propName, propType.toKClass())
                    params.add(
                        PropertySpec
                            .builder(propName, propType.toKClass().asTypeName())
                            .initializer(propName)
                            .mutable(isMutable)
                            .build()
                    )
                }
                else -> {} //ignore
            }
        }

        return TypeSpec.classBuilder(name)
            .addModifiers(modifiers.map { it.toModifier() })
            .primaryConstructor(constructor.build())
            .addProperties(params)
            .build()
    }

    private fun String.isModifier(): Boolean = this in listOf("data", "public", "private", "sealed")
    private fun String.toModifier(): KModifier = when(this) {
        "data"-> KModifier.DATA
        "public" -> KModifier.PUBLIC
        "private"-> KModifier.PRIVATE
        "sealed" -> KModifier.SEALED
        else -> error("Unkown modifier")
    }
    private fun String.isValidName(): Boolean = this.matches("\\w*".toRegex())
    private fun String.toKClass(): KClass<*> = when(this) {
        "String" -> String::class
        "Double" -> Double::class
        "Int" -> Int::class
        else -> error("unregnized type!")
    }

    override fun invoke(atg: ATG): FileSpec {

        val specName = getSpecName(atg)
        val enumName = getEnumName(atg)

        val tokenTypeEnum = TypeSpec.enumBuilder(enumName)
            .addSuperinterface(TokenType::class)
            .apply {
                atg.keywords.forEach { addEnumConstant(getEnumConstantName(it)) }
                atg.tokens.forEach { addEnumConstant(getEnumConstantName(it)) }
                atg.characters.forEach { addEnumConstant(getEnumConstantName(it)) }
            }
            .build()

        val patternsMap = PropertySpec
            .builder("patternsMap", Map::class.asClassName().parameterizedBy(TokenType::class.asTypeName(), StatefulRegex::class.asTypeName()))
            .addModifiers(KModifier.PRIVATE)
            .initializer(
                CodeBlock.builder()
                    .apply {
                        addStatement("mapOf(")
                        atg.keywords.forEach { addStatement("\t$enumName.${getEnumConstantName(it)} to %S.toStatefulRegex(),", it.def) }
                        atg.tokens.forEach { addStatement("\t$enumName.${getEnumConstantName(it)} to %S.toStatefulRegex(),", it.regex) }
                        atg.characters.forEach { encodeMap("$enumName.${getEnumConstantName(it)}", it) }
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
                        atg.keywords.forEach { addStatement("\t%S to ${enumName}.${getEnumConstantName(it)},", it.def) }
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

        val parser = ParserGenerator().invoke(atg)

        val parseFunction = FunSpec.builder("parse")
            .addParameter("source", Stream::class.parameterizedBy(TokenRef::class))
            .addModifiers(KModifier.OVERRIDE)
            .addStatement("${atg.parserName()}(source).parse()")
            .build()

        val spec = TypeSpec.objectBuilder(specName)
            .addSuperinterface(Spec::class)
            .addInitializerBlock(getInitCode(atg))
            .addType(tokenTypeEnum)
            .addType(parser)
            .inferCustomTypes(atg)
            .addProperty(patternsMap)
            .addProperty(keywordsMap)
            .addProperty(ignoreSet)
            .addFunction(allPatterns)
            .addFunction(getKeyword)
            .addFunction(ignore)
            .addFunction(parseFunction)
            .build()

        val mainFunction = FunSpec.builder("main")
            .addParameter("args", Array::class.asClassName().parameterizedBy(String::class.asTypeName()))
            .addStatement("ParserGeneratorMain($specName).main(args)")
            .build()

        return FileSpec.builder("org.github.compiler.generated", specName)
            .addImport("org.github.compiler.regularExpressions.regexImpl", "toStatefulRegex")
            .addImport("org.github.compiler.ui.cli.scannerGenerator", "ParserGeneratorMain")
            .addType(spec)
            .addFunction(mainFunction)
            .build()
    }

}
