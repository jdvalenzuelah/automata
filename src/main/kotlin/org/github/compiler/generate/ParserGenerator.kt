package org.github.compiler.generate

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import org.github.compiler.atg.*
import org.github.compiler.atg.parser.AbstractParser
import org.github.compiler.atg.scanner.streams.Stream
import org.github.compiler.atg.specification.TokenRef
import org.github.compiler.regularExpressions.transforms.Transform

fun ATG.parserName(): String = "${compilerName}Parser"

class ParserGenerator: Transform<ATG, TypeSpec> {

    private var dependencies: Map<String, MutableList<SymbolType.Ident>> = emptyMap()

    private fun resolveDependencies(atg: ATG) {
       dependencies = atg.productions
            .filter { !it.isIndependent(atg) }
            .map { it.name to it.expression.expr.flatMap { f -> f.factors } }
            .mapNotNull { (name, factors) ->
                val newFactors: List<SymbolType.Ident> = factors.mapNotNull {
                    if(it is Factor.Symbol ) {
                        when(it.symbol) {
                            is SymbolType.Ident -> if(!atg.isKnownToken(it.symbol as SymbolType.Ident)) it.symbol as SymbolType.Ident else null
                            else -> null
                        }
                    } else null
                }
                if(newFactors.isEmpty()) null else name to newFactors.toMutableList()
            }.toMap()

        val keys = dependencies.keys.toMutableList()

        val allDeps = dependencies.values.flatMap { it.map { f -> f.name  } }
        while(keys.isNotEmpty()) {
            val key = keys.first()

            if(key !in allDeps) {
                keys.removeFirst()
                continue
            }

            val keyDeps = dependencies[key] ?: mutableListOf()

            dependencies.forEach { (_, deps)  ->
                val exists = deps.firstOrNull { it.name == key } != null
                if(exists) {
                    deps.removeIf { it.name == key }
                    deps.addAll(keyDeps)
                }
            }
            keys.removeFirst()
        }
    }

    private fun Production.getCheckName(): String = name.getCheckName()

    private fun String.getCheckName(): String = "check${this}"

    private fun Production.getFunctionParameters(): Collection<ParameterSpec> {
        return if(attributes.isEmpty()) emptyList() else
            attributes
                .split(",")
                .map { arg ->
                    val nameAndType = arg.split(":")
                    check(nameAndType.size == 2) { "Invalid function parameter $arg" }

                    val name = nameAndType[0].trim()
                    val type = nameAndType[1].trim()
                    ParameterSpec
                        .builder(name, ClassName("", type))
                        .build()
                }
    }

    private fun Expression.isSimple(): Boolean = expr.isSimple()

    private fun Collection<Term>.isSimple(): Boolean = all { term ->
        term.factors.all { it is Factor.Symbol || it is Factor.SemAction }
    }

    private fun Production.getFuncSpecBuilder(): FunSpec.Builder {
        return FunSpec.builder(name)
            .addParameters(getFunctionParameters())
            .addStatement(semanticAction)
    }

    private fun CodeBlock.Builder.expectSymbol(atg: ATG, symbol: Factor.Symbol): CodeBlock.Builder {
        when(symbol.symbol) {
            is SymbolType.Ident -> {
                val ident = symbol.symbol as SymbolType.Ident
                if(atg.isKnownToken(ident)) {
                    addStatement("expect(${getEnumName(atg)}.${getEnumConstantName(ident)})")
                } else {
                    addStatement("${ident.name}(${symbol.attrsCode})")
                }
            }
            is SymbolType.Literal -> {
                val literal = symbol.symbol as SymbolType.Literal
                addStatement("expectLiteral(${literal.def})")
            }
        }
        return this
    }

    private fun CodeBlock.Builder.beginTokenMatch(funName: String, atg: ATG, expression: Collection<Term>, semAction: String? = null): CodeBlock.Builder {
        val enumName = getEnumName(atg)
        if(expression.size == 1) {
            val term = expression.first()
            if(term.factors.size == 1) {
                when(val factor = term.factors.first()) {
                    is Factor.Symbol -> {
                        expectSymbol(atg, factor)
                        semAction?.let { addStatement(it) }
                    }
                    is Factor.SemAction -> {
                        addStatement(factor.code)
                        semAction?.let { addStatement(it) }
                    }
                }
                return this
            }
        }

        beginControlFlow("when")
        expression.forEach { term ->
            val factorsMutable = term.factors.toMutableList()

            val first = factorsMutable.removeFirst()

            if(first is Factor.Symbol) {
                symbolCheck(atg, first.symbol, enumName)?.also { check ->
                    beginControlFlow("$check ->")
                    this.expectSymbol(atg, first)
                    factorsMutable.forEach { factor ->
                        when {
                            factor is Factor.Symbol -> this.expectSymbol(atg, factor)
                            factor is Factor.SemAction -> this.addStatement(factor.code)
                        }
                    }
                    semAction?.let { addStatement(it) }
                    endControlFlow()
                }
            }
        }
        addStatement("else -> synError(%S)", "invalid $funName")
        endControlFlow()

        return this
    }

    private fun CodeBlock.Builder.addExpressionBody(funName: String, atg: ATG, expression: Collection<Term>, exprSemActionCode: String? = null): CodeBlock.Builder {

        val expressionMutable = expression.toMutableList()

        val semanticAction = expressionMutable.firstOrNull { it.factors.first() is Factor.SemAction }

        if(semanticAction != null)
            expressionMutable.remove(semanticAction)

        val semanticActionCode = semanticAction?.factors
            ?.map { it as Factor.SemAction }
            ?.joinToString(separator = ";") { it.code }
            ?: exprSemActionCode

        val enumName = getEnumName(atg)
        expressionMutable.forEach { term ->
            val termMutable = term.factors.toMutableList()

            val termSemAction = termMutable.firstOrNull { it is Factor.SemAction }
            if(termSemAction != null)
                termMutable.remove(termSemAction)

            val termSemActionCode = (termSemAction as? Factor.SemAction)?.code
            termMutable.forEach { factor ->
                when(factor) {
                    is Factor.SemAction -> addStatement(factor.code)
                    is Factor.Repeat -> {
                        val checks = factor.expr.getAllSymbols()
                            .mapNotNull { symbolCheck(atg, it.symbol, enumName) }
                            .joinToString(separator = " || ")

                        beginControlFlow("while($checks)")
                        if(factor.expr.isSimple())
                            this.beginTokenMatch(funName, atg, factor.expr.expr, termSemActionCode)
                        else
                            this.addExpressionBody(funName, atg, factor.expr.expr, termSemActionCode)
                        endControlFlow()

                    }
                    is Factor.Grouped -> {
                        if(factor.expr.isSimple()) {
                            this.beginTokenMatch(funName, atg, factor.expr.expr, termSemActionCode)
                        }
                        else
                            this.addExpressionBody(funName, atg, factor.expr.expr)
                    }
                    is Factor.Optional -> {
                        val checks = factor.expr.getAllSymbols()
                            .mapNotNull { symbolCheck(atg, it.symbol, enumName) }
                            .joinToString(separator = " || ")

                        beginControlFlow("if($checks)")
                        this.addExpressionBody(funName, atg, factor.expr.expr)
                        endControlFlow()
                    }
                    is Factor.Symbol -> {
                        when(factor.symbol) {
                            is SymbolType.Literal -> {
                                val literal = factor.symbol as SymbolType.Literal
                                addStatement("expectLiteral(${literal.def})")
                            }
                            is SymbolType.Ident -> {
                                val ident = factor.symbol as SymbolType.Ident
                                if(atg.isKnownToken(ident)) {
                                    addStatement("expect(${enumName}.${getEnumConstantName(ident)})")
                                } else {
                                    addStatement("${ident.name}(${factor.attrs.joinToString(separator = "") { it.lexeme }})")
                                }
                            }
                        }
                        if(termSemActionCode != null) {
                            addStatement(termSemActionCode)
                        }
                    }
                }
            }
        }
        semanticActionCode?.also { addStatement(it) }
        return this
    }

    private fun FunSpec.Builder.addExpression(funName: String, atg: ATG, expression: Expression): FunSpec.Builder {
        val body = CodeBlock.builder()
            .addExpressionBody(funName, atg, expression.expr)
            .build()

        addCode(body)

        return this
    }

    private fun Expression.getAllSymbols(): Collection<Factor.Symbol> {
        return expr.flatMap { it.factors }
            .flatMap {
                when(it) {
                    is Factor.Symbol -> listOf(it)
                    is Factor.SemAction -> emptyList()
                    is Factor.Optional -> it.expr.getAllSymbols()
                    is Factor.Repeat -> it.expr.getAllSymbols()
                    is Factor.Grouped -> it.expr.getAllSymbols()
                }
            }
    }

    private fun symbolCheck(atg: ATG, symbolType: SymbolType?, enumName: String, getFnCheck: Boolean = true): String? = when(symbolType) {
        is SymbolType.Literal -> "lookAhead.lexeme == ${symbolType.def}"
        is SymbolType.Ident -> {
            when {
                atg.isKnownToken(symbolType) -> "lookAhead.type == ${enumName}.${getEnumConstantName(symbolType)}"
                atg.isIndependent(symbolType) || getFnCheck -> {
                    if(dependencies[symbolType.name] != null) {
                        dependencies[symbolType.name]?.joinToString(separator = " || ") {
                            "${it.name.getCheckName()}()"
                        }
                    } else
                        "${symbolType.name.getCheckName()}()"
                }
                else -> null
            }
        }
        else -> null
    }

    private fun productionCheck(atg: ATG, production: Production): FunSpec.Builder? {
        val enumName = getEnumName(atg)
        val symbols = production.expression.getAllSymbols()

        val checks = symbols.mapNotNull { s ->
            symbolCheck(atg, s.symbol, enumName, true)
        }
            .filterNot { it == "check${production.name}()" }
            .distinct()

        return if(checks.isEmpty())
            null
        else
            FunSpec.builder(production.getCheckName())
            .addComment("""
                avoid single-expression function
            """.trimIndent())
            .addStatement("return ${checks.joinToString(separator = " || ").replace("\n", "")}")
            .returns(Boolean::class.asTypeName())

    }

    override fun invoke(atg: ATG): TypeSpec {
        resolveDependencies(atg)

        val checkFunctions = mutableListOf<FunSpec>()
        val parsingFunctions = mutableListOf<FunSpec>()

        atg.productions.forEach {
            productionCheck(atg, it)?.let { ch -> checkFunctions.add(ch.build()) }

            parsingFunctions.add(
                it.getFuncSpecBuilder()
                    .addExpression(it.name, atg, it.expression)
                    .build()
            )
        }

        val entryPoint = parsingFunctions.first().name

        val parsingFunction = FunSpec.builder("parse")
            .addModifiers(KModifier.OVERRIDE)
            .addCode(
                CodeBlock.builder()
                    .beginControlFlow("while(hasNext())")
                    .addStatement("$entryPoint()")
                    .endControlFlow()
                    .build()
            )
            .build()

        val parserConstructor = FunSpec.constructorBuilder()
            .addParameter("tokenStream", Stream::class.parameterizedBy(TokenRef::class))
            .build()

        return TypeSpec.classBuilder(atg.parserName())
            .superclass(AbstractParser::class.parameterizedBy(TokenRef::class, Unit::class))
            .addSuperclassConstructorParameter(CodeBlock.of("tokenStream"))
            .primaryConstructor(parserConstructor)
            .addFunction(parsingFunction)
            .addFunctions(checkFunctions)
            .addFunctions(parsingFunctions)
            .build()

    }

}
