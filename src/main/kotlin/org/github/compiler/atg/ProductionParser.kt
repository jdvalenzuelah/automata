package org.github.compiler.atg

import org.github.compiler.atg.parser.AbstractParser
import org.github.compiler.atg.scanner.streams.Stream
import org.tinylog.kotlin.Logger

class ProductionParser(
    productionTokens: Stream<Token>
): AbstractParser<Token, Collection<Production>>(productionTokens) {

    private data class ProductionDecl(
        var name: String = "",
        var attributes: MutableList<Token> = mutableListOf(),
        var semAction: MutableList<Token> = mutableListOf(),
        var expression: Expression = Expression()
    )

    override fun parse(): Collection<Production> {
        val productions = parserSpecification()

        errors.forEach { Logger.error(it.message) }

        return productions.map { decl ->
           Production(
               decl.name,
               decl.attributes.joinToString(separator = "") { it.lexeme },
               decl.semAction.joinToString(separator = "") { it.lexeme },
               decl.expression
           )
        }
    }

    private fun fatalError(msg: String): Nothing = throw Exception(msg)

    private fun parserSpecification(): Collection<ProductionDecl> {
        val productions = mutableListOf<ProductionDecl>()
        expect(ATGTokenType.PRODUCTIONS)

        if(errors.isNotEmpty()) {
            fatalError(errors.first().toString())
        }

        while (hasNext() && lookAhead.type != ATGTokenType.END) {
            val newProduction = ProductionDecl()
            production(newProduction)
            productions.add(newProduction)
        }

        return productions
    }

    private fun production(production: ProductionDecl) {

        if(lookAhead.type == ATGTokenType.IDENT) {
            expect(ATGTokenType.IDENT)
            production.name = lastToken!!.lexeme
        } else
            fatalError("Un named production!")

        if(lookAhead.type == ATGTokenType.START_ATTR) {
            val attrs = mutableListOf<Token>()
            attributes(attrs)
            production.attributes.addAll(attrs)
        }

        if(lookAhead.type == ATGTokenType.START_CODE) {
            val semAction = mutableListOf<Token>()
            semAction(semAction)
            production.semAction.addAll(semAction)
        }

        expect(ATGTokenType.EQUALS)

        expression(production)

        expect(ATGTokenType.DOT)

    }

    private fun attributes(attrs: MutableList<Token>) {
        expect(ATGTokenType.START_ATTR)

        while(lookAhead.type != ATGTokenType.END_ATTR) {
            expect(lookAhead.type)
            attrs.add(lastToken!!)
        }

        expect(ATGTokenType.END_ATTR)
    }

    private fun semAction(sem: MutableList<Token>) {
        expect(ATGTokenType.START_CODE)

        while (lookAhead.type != ATGTokenType.END_CODE) {
            expect(lookAhead.type)
            sem.add(lastToken!!)
        }

        expect(ATGTokenType.END_CODE)

    }

    private fun expression(production: ProductionDecl) {
        val expr = Expression()
        expression(expr)
        production.expression = expr
    }

    private fun expression(expr: Expression) {
        term(expr)

        while (lookAhead.type == ATGTokenType.PIPE) {
            expect(ATGTokenType.PIPE)
            term(expr)
        }
    }

    private fun term(expr: Expression) {
        val term = Term()
        factor(term)
        expr.expr.add(term)

        while (lookAhead.type in listOf(
                ATGTokenType.STRING, ATGTokenType.CHAR, ATGTokenType.IDENT,
                ATGTokenType.PARENTHESIS_OPEN, ATGTokenType.CURLY_BRACKET_OPEN,
                ATGTokenType.BRACKET_OPEN, ATGTokenType.START_CODE
        )) {
            val newTerm = Term()
            factor(newTerm)
            expr.expr.add(newTerm)
        }

    }

    private fun factor(term: Term) {

        when(lookAhead.type) {
            ATGTokenType.STRING, ATGTokenType.CHAR, ATGTokenType.IDENT -> {
                val sym = Factor.Symbol()
                symbol(sym)
                term.factors.add(sym)
            }
            ATGTokenType.PARENTHESIS_OPEN -> {
                expect(ATGTokenType.PARENTHESIS_OPEN)
                val newExpr = Expression()
                expression(newExpr)
                term.factors.add(Factor.Grouped(newExpr))
                expect(ATGTokenType.PARENTHESIS_CLOSE)
            }
            ATGTokenType.CURLY_BRACKET_OPEN -> {
                expect(ATGTokenType.CURLY_BRACKET_OPEN)
                val newExpr = Expression()
                expression(newExpr)
                term.factors.add(Factor.Repeat(newExpr))
                expect(ATGTokenType.CURLY_BRACKET_CLOSE)
            }
            ATGTokenType.BRACKET_OPEN -> {
                expect(ATGTokenType.BRACKET_OPEN)
                val newExpr = Expression()
                expression(newExpr)
                term.factors.add(Factor.Optional(newExpr))
                expect(ATGTokenType.BRACKET_CLOSE)
            }
            ATGTokenType.START_CODE -> {
                val action = mutableListOf<Token>()
                semAction(action)
                term.factors.add(Factor.SemAction(action))
            }
            else -> synError("malformed factor")
        }

    }

    private fun symbol(sym: Factor.Symbol) {
        when(lookAhead.type) {
            ATGTokenType.STRING -> {
                expect(ATGTokenType.STRING)
                sym.symbol = SymbolType.Literal(lastToken!!.lexeme)
            }
            ATGTokenType.CHAR -> {
                expect(ATGTokenType.CHAR)
                sym.symbol = SymbolType.Literal(lastToken!!.lexeme)
            }
            ATGTokenType.IDENT -> {
                expect(ATGTokenType.IDENT)
                sym.symbol = SymbolType.Ident(lastToken!!.lexeme)
            }
            else -> synError("incorrect symbol")
        }

        if(lookAhead.type == ATGTokenType.START_ATTR) {
            val attrs = mutableListOf<Token>()
            attributes(attrs)
            sym.attrs.addAll(attrs)
        }
    }

}
