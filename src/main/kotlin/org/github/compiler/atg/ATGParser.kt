package org.github.compiler.atg

import org.github.compiler.regularExpressions.Regex
import org.github.compiler.regularExpressions.regex.tokenize.escape

class ATGParser(
    tokens: Collection<Token>
) {

    init {
        require(tokens.isNotEmpty()) { "Tokens are require to perform parsing!" }
    }

    private data class SetDecl(val ident: Token, val def: Collection<Token>)
    private data class KeywordDecl(val ident: Token, val def: Token)
    private data class TokenDecl(val ident: Token, val def: Collection<Token>)

    private val tokens = ArrayDeque<Token>().apply { addAll(tokens) }

    private val charsLookup = mutableMapOf("ANY" to Character("ANY", ATGSpec.ANY))
    private val tokenLookup = mutableMapOf<String, TokenDef>()

    fun parse(): ATG {
        val compilerName = getCompilerName()
        val characters = parseCharactersDecls(getCharacters())
        val keywords = parseKeywordDecls(getKeywords())
        val tokens = parseTokens(getTokens())
        val ignore = parseIgnoreSet(getIgnoreSet())

        return ATG(compilerName, characters, keywords, tokens, ignore)
    }

    private fun getCompilerName(): String {
        // Check compiler def
        check(tokens.removeFirst().type == TokenType.COMPILER)

        val compilerName = tokens.removeFirst()

        // Check compiler name is an ident
        check(compilerName.type == TokenType.IDENT)

        return compilerName.lexeme
    }

    private fun getCharacters(): Collection<SetDecl> {
        // Ignore everything until char definition!
        while (tokens.first().type != TokenType.CHARACTERS) {
            tokens.removeFirst()
        }

        tokens.removeFirst() //Char definition

        val characters = mutableListOf<SetDecl>()
        // read until we reach keywords
        while (tokens.first().type !in listOf(TokenType.KEYWORDS, TokenType.TOKENS) && isNotEnded()) {
            characters.add(readSetDecl())
        }

        return characters
    }

    private fun getKeywords(): Collection<KeywordDecl> {
        if(tokens.first().type == TokenType.TOKENS) return emptyList()

        check(tokens.first().type == TokenType.KEYWORDS)
        tokens.removeFirst() // keywords start

        val keywords = mutableListOf<KeywordDecl>()
        // read until we reach tokens
        while (tokens.first().type != TokenType.TOKENS && isNotEnded()) {
            keywords.add(readKeyword())
        }

        return keywords
    }

    private fun getTokens(): Collection<TokenDecl> {
        check(tokens.first().type == TokenType.TOKENS)
        tokens.removeFirst() // tokens start

        val tokensD = mutableListOf<TokenDecl>()
        // read until we reach white space decl
        while (tokens.first().type != TokenType.IGNORE && isNotEnded()) {
            tokensD.add(readToken())
        }

        return tokensD
    }

    private fun getIgnoreSet(): Collection<Token> {
        if(!isNotEnded()) return emptyList()
        check(tokens.first().type == TokenType.IGNORE)
        tokens.removeFirst() // ignore start
        return readSet()
    }

    private fun readToken(): TokenDecl {
        check(tokens.first().type == TokenType.IDENT)
        val ident = tokens.removeFirst()

        check(tokens.first().type == TokenType.EQUALS)
        tokens.removeFirst() // Ignore =

        val tokenDef = mutableListOf<Token>()
        while (tokens.first().type != TokenType.DOT && isNotEnded()) {
            tokenDef.add(tokens.removeFirst())
        }
        tokens.removeFirst() //Ignore dot
        return TokenDecl(ident, tokenDef)
    }

    private fun readKeyword(): KeywordDecl {
        check(tokens.first().type === TokenType.IDENT)
        val ident = tokens.removeFirst()

        check(tokens.first().type == TokenType.EQUALS)
        tokens.removeFirst() //Ignore =

        check(tokens.first().type == TokenType.STRING)
        val def = tokens.removeFirst()

        check(tokens.first().type == TokenType.DOT)
        tokens.removeFirst() //ignore dot

        return KeywordDecl(ident, def)
    }

    private fun readSetDecl(): SetDecl {
        check(tokens.first().type === TokenType.IDENT)
        val ident = tokens.removeFirst()

        check(tokens.first().type == TokenType.EQUALS)
        tokens.removeFirst() //Ignore =

        val set = readSet()
        check(set.isNotEmpty())

        return SetDecl(ident, set)
    }

    private fun readSet(): Collection<Token> {
        val set = mutableListOf<Token>()
        do {
            set.addAll(readBasicSet())

            val op = if(tokens.first().type in listOf(TokenType.PLUS, TokenType.MINUS)) tokens.removeFirst() else null

            if(op != null) {
                val secondSet = readBasicSet()
                check(secondSet.isNotEmpty())
                set.add(op)
                set.addAll(secondSet)
            }

        } while (tokens.first().type != TokenType.DOT && isNotEnded())

        tokens.removeFirst() //Ignore dot
        return set
    }

    private fun readBasicSet(): Collection<Token> {
        val acc = mutableListOf<Token>()
        while(tokens.first().type in listOf(TokenType.STRING, TokenType.IDENT, TokenType.CHAR, TokenType.CHAR_NUMBER, TokenType.CHAR_NUMBER_INTERVAL,  TokenType.CHAR_INTERVAL, TokenType.ANY)) {
            val tok = tokens.removeFirst()
            val tokenToAdd = if(tok.type == TokenType.ANY) tok.copy(type = TokenType.CHAR, lexeme = ATGSpec.ANY) else tok
           acc.add(tokenToAdd)
        }
        return acc
    }

    private fun isNotEnded(): Boolean = tokens.firstOrNull()?.type !in listOf(TokenType.END, null)

    private fun parseKeywordDecls(keywordDecls: Collection<KeywordDecl>): Collection<Keyword> {
        return keywordDecls.map(::parseKeywordDecls)
    }

    private fun parseKeywordDecls(decl: KeywordDecl): Keyword {
        check(decl.ident.type == TokenType.IDENT)
        check(decl.def.type == TokenType.STRING)

        val id = decl.ident.lexeme
        val def = extractString(decl.def)

        return Keyword(id, def)
    }

    private fun parseCharactersDecls(chars: Collection<SetDecl>): List<Character> {
        val singleDef = chars.filter { it.def.size == 1 }
            .map { decl ->
                check(decl.ident.type == TokenType.IDENT)
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

    private fun aggregateSet(all: Collection<Token>): String {
        //TODO: Improve this mess
        var currOp: String? = null
        var acc = ""

        for(token in all) {
            when {
                token.type == TokenType.PLUS -> { currOp = "+"; continue }
                token.type == TokenType.MINUS -> { currOp = "-"; continue }
                currOp == null -> {
                    acc = if(token.type == TokenType.IDENT)
                        charsLookup[token.lexeme]?.def ?: ""
                    else parseSingleTokenAsRegex(token)!!
                }
                currOp == "+" -> {
                    val operand = if(token.type == TokenType.IDENT)
                        charsLookup[token.lexeme]?.def ?: ""
                    else parseSingleTokenAsRegex(token)!!
                    acc = (acc.toList() + operand.toList()).joinToString(separator = "")
                }
                currOp == "-" -> {
                    val operand = if(token.type == TokenType.IDENT)
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
        check(tokenDecl.ident.type == TokenType.IDENT)
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
            TokenType.STRING -> extractString(token).map { Regex.escape(it.toString()) }.joinToString(separator = "")
            TokenType.CHAR -> extractChar(token)
            TokenType.CHAR_NUMBER -> extractCharNumber(token)
            TokenType.CHAR_NUMBER_INTERVAL, TokenType.CHAR_INTERVAL -> extractCharInterval(token)
            TokenType.IDENT -> charsLookup[token.lexeme]?.asRegexExpression() ?: tokenLookup[token.lexeme]?.regex ?: ""
            TokenType.PARENTHESIS_OPEN -> "("
            TokenType.PARENTHESIS_CLOSE -> ")"
            TokenType.CURLY_BRACKET_OPEN -> "("
            TokenType.CURLY_BRACKET_CLOSE -> ")*"
            TokenType.BRACKET_OPEN -> "("
            TokenType.BRACKET_CLOSE -> ")?"
            TokenType.PIPE -> "|"
            TokenType.EXCEPT, TokenType.KEYWORDS -> null
            else -> error("unsupported token=$token")
        }
    }

    private fun extractString(token: Token): String {
        require(token.type == TokenType.STRING)
        return token.lexeme //Replace initial and ending " and escaped characters
            .removePrefix("\"")
            .removeSuffix("\"")
            .replace("\\", "")
    }

    private fun extractChar(token: Token): String {
        require(token.type == TokenType.CHAR)
        return token.lexeme
            .removePrefix("'")
            .removeSuffix("'")
            .replace("/", "")
    }

    private fun extractCharNumber(token: Token): String {
        require(token.type == TokenType.CHAR_NUMBER)

        return token.lexeme
            .removePrefix("CHR(")
            .removeSuffix(")")
            .toInt()
            .toChar()
            .toString()
    }

    private fun extractCharInterval(token: Token): String {
        require(token.type in listOf(TokenType.CHAR_NUMBER_INTERVAL, TokenType.CHAR_INTERVAL))

        val trim: (String) -> Int = if(token.type == TokenType.CHAR_NUMBER_INTERVAL) {
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
