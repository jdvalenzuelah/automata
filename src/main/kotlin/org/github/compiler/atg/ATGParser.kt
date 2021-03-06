package org.github.compiler.atg

import org.github.compiler.atg.scanner.streams.ArrayDequeStream
import org.github.compiler.regularExpressions.Regex
import org.github.compiler.regularExpressions.regex.tokenize.escape

class ATGParser(
    allTokens: Collection<Token>
) {

    init {
        require(allTokens.isNotEmpty()) { "Tokens are require to perform parsing!" }
    }

    private data class SetDecl(val ident: Token, val def: Collection<Token>)
    private data class KeywordDecl(val ident: Token, val def: Token)
    private data class TokenDecl(val ident: Token, val def: Collection<Token>)

    private data class ProductionDecl(
        val ident: Token,
        val attributes: Collection<Token>,
        val semanticActions: Collection<Token>,
        val expression: Collection<Token>
    )

    private val tokens = ArrayDeque<Token>().apply { addAll(allTokens) }

    private val charsLookup = mutableMapOf("ANY" to Character("ANY", ATGSpec.ANY))
    private val tokenLookup = mutableMapOf<String, TokenDef>()

    private val codeBlocks = mutableListOf<Token>()

    fun parse(): ATG {
        val compilerName = getCompilerName()
        val characters = parseCharactersDecls(getCharacters())
        val keywords = parseKeywordDecls(getKeywords())
        val tokens = parseTokens(getTokens())
        val ignore = parseIgnoreSet(getIgnoreSet())
        val productions = ProductionParser(ArrayDequeStream(this.tokens)).parse()
        val code = parseCodeBlocks()

        return ATG(compilerName, characters, keywords, tokens, ignore, productions, code)
    }

    private fun getCompilerName(): String {
        // Check compiler def
        check(tokens.removeFirst().type == ATGTokenType.COMPILER)

        val compilerName = tokens.removeFirst()

        // Check compiler name is an ident
        check(compilerName.type == ATGTokenType.IDENT)

        return compilerName.lexeme
    }

    private fun getCharacters(): Collection<SetDecl> {
        while (tokens.first().type != ATGTokenType.CHARACTERS) {
            if(tokens.first().type == ATGTokenType.START_CODE)
                consumeCode()
            else
                tokens.removeFirst() // Ignore everything until char definition!
        }

        tokens.removeFirst() //Char definition

        val characters = mutableListOf<SetDecl>()
        // read until we reach keywords
        while (tokens.first().type !in listOf(ATGTokenType.KEYWORDS, ATGTokenType.TOKENS) && isNotEnded()) {
            characters.add(readSetDecl())
        }

        return characters
    }

    private fun consumeCode() {
        check(tokens.first().type == ATGTokenType.START_CODE)

        tokens.removeFirst() // Ignore (.

        while(tokens.first().type != ATGTokenType.END_CODE) {
            codeBlocks.add(tokens.removeFirst())
        }

        tokens.removeFirst() // Ignore .)
    }

    private fun getKeywords(): Collection<KeywordDecl> {
        if(tokens.first().type == ATGTokenType.TOKENS) return emptyList()

        check(tokens.first().type == ATGTokenType.KEYWORDS)
        tokens.removeFirst() // keywords start

        val keywords = mutableListOf<KeywordDecl>()
        // read until we reach tokens
        while (tokens.first().type != ATGTokenType.TOKENS && isNotEnded()) {
            keywords.add(readKeyword())
        }

        return keywords
    }

    private fun getTokens(): Collection<TokenDecl> {
        check(tokens.first().type == ATGTokenType.TOKENS)
        tokens.removeFirst() // tokens start

        val tokensD = mutableListOf<TokenDecl>()
        // read until we reach white space decl
        while (tokens.first().type !in listOf(ATGTokenType.IGNORE, ATGTokenType.PRODUCTIONS) && isNotEnded()) {
            tokensD.add(readToken())
        }

        return tokensD
    }

    private fun getIgnoreSet(): Collection<Token> {
        if(!isNotEnded() || tokens.first().type == ATGTokenType.PRODUCTIONS)
            return emptyList()

        check(tokens.first().type == ATGTokenType.IGNORE)
        tokens.removeFirst() // ignore start
        return readSet()
    }

    private fun getProductions(): Collection<ProductionDecl> {
        if(!isNotEnded())
            return emptyList()

        check(tokens.first().type == ATGTokenType.PRODUCTIONS)
        tokens.removeFirst() // ignore start

        val decls = mutableListOf<ProductionDecl>()
        while (isNotEnded() && tokens.first().type != ATGTokenType.END) {
            decls.add(readProduction())
        }
        return decls
    }

    private fun readProduction(): ProductionDecl {
        check(tokens.first().type == ATGTokenType.IDENT)

        val productionName = tokens.removeFirst()

        val attrs = if(tokens.first().type == ATGTokenType.START_ATTR)
            readAttributes()
        else
            emptyList()

        val semAction = if(tokens.first().type == ATGTokenType.START_CODE)
            readSemAction()
        else
            emptyList()

        check(tokens.first().type == ATGTokenType.EQUALS)

        tokens.removeFirst() // Ignore =

        val expr = readExpr()

        check(tokens.first().type == ATGTokenType.DOT)
        tokens.removeFirst() // Ignore dot

        return ProductionDecl(productionName, attrs, semAction, expr)
    }

    private fun readExpr(): Collection<Token> {

        val decl = mutableListOf<Token>()
        while(tokens.first().type != ATGTokenType.DOT) {
            decl.add(tokens.removeFirst())
        }

        return decl
    }

    private fun readSemAction(): Collection<Token> {
        check(tokens.first().type == ATGTokenType.START_CODE)

        tokens.removeFirst() // Ignore start code

        val code = mutableListOf<Token>()
        while(tokens.first().type != ATGTokenType.END_CODE) {
            code.add(tokens.removeFirst())
        }

        check(tokens.first().type == ATGTokenType.END_CODE)

        tokens.removeFirst() // Ignore end code

        return code
    }

    private fun readAttributes(): Collection<Token> {
        check(tokens.first().type == ATGTokenType.START_ATTR)
        tokens.removeFirst() // ignore start

        val attrs = mutableListOf<Token>()
        while (tokens.first().type != ATGTokenType.END_ATTR) {
            attrs.add(tokens.removeFirst())
        }

        check(tokens.first().type == ATGTokenType.END_ATTR)
        tokens.removeFirst() // Ignore end
        return attrs
    }

    private fun readToken(): TokenDecl {
        check(tokens.first().type == ATGTokenType.IDENT)
        val ident = tokens.removeFirst()

        check(tokens.first().type == ATGTokenType.EQUALS)
        tokens.removeFirst() // Ignore =

        val tokenDef = mutableListOf<Token>()
        while (tokens.first().type != ATGTokenType.DOT && isNotEnded()) {
            tokenDef.add(tokens.removeFirst())
        }
        tokens.removeFirst() //Ignore dot
        return TokenDecl(ident, tokenDef)
    }

    private fun readKeyword(): KeywordDecl {
        check(tokens.first().type === ATGTokenType.IDENT)
        val ident = tokens.removeFirst()

        check(tokens.first().type == ATGTokenType.EQUALS)
        tokens.removeFirst() //Ignore =

        check(tokens.first().type == ATGTokenType.STRING)
        val def = tokens.removeFirst()

        check(tokens.first().type == ATGTokenType.DOT)
        tokens.removeFirst() //ignore dot

        return KeywordDecl(ident, def)
    }

    private fun readSetDecl(): SetDecl {
        check(tokens.first().type === ATGTokenType.IDENT)
        val ident = tokens.removeFirst()

        check(tokens.first().type == ATGTokenType.EQUALS)
        tokens.removeFirst() //Ignore =

        val set = readSet()
        check(set.isNotEmpty())

        return SetDecl(ident, set)
    }

    private fun readSet(): Collection<Token> {
        val set = mutableListOf<Token>()
        do {
            set.addAll(readBasicSet())

            val op = if(tokens.first().type in listOf(ATGTokenType.PLUS, ATGTokenType.MINUS)) tokens.removeFirst() else null

            if(op != null) {
                val secondSet = readBasicSet()
                check(secondSet.isNotEmpty())
                set.add(op)
                set.addAll(secondSet)
            }

        } while (tokens.first().type != ATGTokenType.DOT && isNotEnded())

        tokens.removeFirst() //Ignore dot
        return set
    }

    private fun readBasicSet(): Collection<Token> {
        val acc = mutableListOf<Token>()
        while(tokens.first().type in listOf(ATGTokenType.STRING, ATGTokenType.IDENT, ATGTokenType.CHAR, ATGTokenType.CHAR_NUMBER, ATGTokenType.CHAR_NUMBER_INTERVAL,  ATGTokenType.CHAR_INTERVAL, ATGTokenType.ANY)) {
            val tok = tokens.removeFirst()
            val tokenToAdd = if(tok.type == ATGTokenType.ANY) tok.copy(type = ATGTokenType.CHAR, lexeme = ATGSpec.ANY) else tok
           acc.add(tokenToAdd)
        }
        return acc
    }

    private fun isNotEnded(): Boolean = tokens.firstOrNull()?.type !in listOf(ATGTokenType.END, null)

    private fun parseKeywordDecls(keywordDecls: Collection<KeywordDecl>): Collection<Keyword> {
        return keywordDecls.map(::parseKeywordDecls)
    }

    private fun parseKeywordDecls(decl: KeywordDecl): Keyword {
        check(decl.ident.type == ATGTokenType.IDENT)
        check(decl.def.type == ATGTokenType.STRING)

        val id = decl.ident.lexeme
        val def = extractString(decl.def)

        return Keyword(id, def)
    }

    private fun parseCharactersDecls(chars: Collection<SetDecl>): List<Character> {
        val singleDef = chars.filter { it.def.size == 1 }
            .map { decl ->
                check(decl.ident.type == ATGTokenType.IDENT)
                val id = decl.ident.lexeme
                val def = parseSingleTokenAsRegex(decl.def.first())!!
                val char = Character(id, def)
                charsLookup[id] = char
                char
            }

        val composed = chars.filter { it.def.size > 1 }
            .map { decl ->
                val id = decl.ident.lexeme
                val def = aggregateSet(decl.def)
                val char = Character(id, def)
                charsLookup[id] = char
                char
            }

        return singleDef + composed
    }

    private fun parseIgnoreSet(decls: Collection<Token>): Character {
        val def = if(decls.size == 1) parseSingleTokenAsRegex(decls.first())!! else aggregateSet(decls)
        return Character("", def)
    }

    private fun parseCodeBlocks(): List<String> = codeBlocks.map { it.lexeme }

    private fun aggregateSet(all: Collection<Token>): String {
        //TODO: Improve this mess
        var currOp: String? = null
        var acc = ""

        for(token in all) {
            when {
                token.type == ATGTokenType.PLUS -> { currOp = "+"; continue }
                token.type == ATGTokenType.MINUS -> { currOp = "-"; continue }
                currOp == null -> {
                    acc = if(token.type == ATGTokenType.IDENT)
                        charsLookup[token.lexeme]?.def ?: ""
                    else parseSingleTokenAsRegex(token)!!
                }
                currOp == "+" -> {
                    val operand = if(token.type == ATGTokenType.IDENT)
                        charsLookup[token.lexeme]?.def ?: ""
                    else parseSingleTokenAsRegex(token)!!
                    acc = (acc.toList() + operand.toList()).joinToString(separator = "")
                }
                currOp == "-" -> {
                    val operand = if(token.type == ATGTokenType.IDENT)
                        charsLookup[token.lexeme]?.def ?: ""
                    else parseSingleTokenAsRegex(token)!!
                    acc = (acc.toList() - operand.toList()).joinToString(separator = "")
                }
            }
        }

        return acc
    }

    private fun parseTokens(toks: Collection<TokenDecl>): Collection<TokenDef> {
        return toks.map(::parseTokenDecl)
    }

    private fun parseTokenDecl(tokenDecl: TokenDecl): TokenDef {
        check(tokenDecl.ident.type == ATGTokenType.IDENT)
        check(tokenDecl.def.isNotEmpty())

        val id = tokenDecl.ident.lexeme
        val tokenPattern = tokenDecl.def
            .joinToString(separator = "") {
                val pat = parseSingleTokenAsRegex(it)
                when(pat) {
                    null, "" -> ""
                    "(", ")", ")*", "|", ")?" -> pat
                    else -> "($pat)"
                }
            }

        val token = TokenDef(id, tokenPattern)
        tokenLookup[id] = token
        return token

    }

    private fun parseSingleTokenAsRegex(token: Token): String? {
        return when(token.type) {
            ATGTokenType.STRING -> extractString(token).map { Regex.escape(it.toString()) }.joinToString(separator = "")
            ATGTokenType.CHAR -> extractChar(token)
            ATGTokenType.CHAR_NUMBER -> extractCharNumber(token)
            ATGTokenType.CHAR_NUMBER_INTERVAL, ATGTokenType.CHAR_INTERVAL -> extractCharInterval(token)
            ATGTokenType.IDENT -> charsLookup[token.lexeme]?.asRegexExpression() ?: tokenLookup[token.lexeme]?.regex ?: ""
            ATGTokenType.PARENTHESIS_OPEN -> "("
            ATGTokenType.PARENTHESIS_CLOSE -> ")"
            ATGTokenType.CURLY_BRACKET_OPEN -> "("
            ATGTokenType.CURLY_BRACKET_CLOSE -> ")*"
            ATGTokenType.BRACKET_OPEN -> "("
            ATGTokenType.BRACKET_CLOSE -> ")?"
            ATGTokenType.PIPE -> "|"
            ATGTokenType.EXCEPT, ATGTokenType.KEYWORDS -> null
            else -> error("unsupported token=$token")
        }
    }

    private fun extractString(token: Token): String {
        require(token.type == ATGTokenType.STRING)
        return token.lexeme //Replace initial and ending " and escaped characters
            .removePrefix("\"")
            .removeSuffix("\"")
            .replace("\\", "")
    }

    private fun extractChar(token: Token): String {
        require(token.type == ATGTokenType.CHAR)
        return token.lexeme
            .removePrefix("'")
            .removeSuffix("'")
            .replace("/", "")
    }

    private fun extractCharNumber(token: Token): String {
        require(token.type == ATGTokenType.CHAR_NUMBER)

        return token.lexeme
            .removePrefix("CHR(")
            .removeSuffix(")")
            .toInt()
            .toChar()
            .toString()
    }

    private fun extractCharInterval(token: Token): String {
        require(token.type in listOf(ATGTokenType.CHAR_NUMBER_INTERVAL, ATGTokenType.CHAR_INTERVAL))

        val trim: (String) -> Int = if(token.type == ATGTokenType.CHAR_NUMBER_INTERVAL) {
            { str: String -> str.removePrefix("CHR(").removeSuffix(")").toInt() }
        } else {
            { str: String -> str.replace("'", "").replace("/", "").first().toInt() }
        }

        val interval = token.lexeme.split("..")
        check(interval.size == 2)

        val (start, end) = interval.map(trim)

        return (start..end).joinToString(separator = "") { it.toChar().toString() }

    }

}
