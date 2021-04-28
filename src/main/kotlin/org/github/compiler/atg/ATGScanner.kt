package org.github.compiler.atg

import org.github.compiler.regularExpressions.regexImpl.StatefulRegex
import org.github.compiler.regularExpressions.regexImpl.toStatefulRegex

//TODO: Detect errors
class ATGScanner(
    private val source: String
) {

    private val tokens = mutableListOf<Token>()

    private var start = 0
    private var current = 0

    private companion object Patterns {
        val ident by lazy { ATGSpec.Patterns.ident.toStatefulRegex() }
        val string by lazy { ATGSpec.Patterns.string.toStatefulRegex() }
        val char by lazy { ATGSpec.Patterns.char.toStatefulRegex() }
        val charNumber by lazy { ATGSpec.Patterns.charNumber.toStatefulRegex() }
        val charNumberInterval by lazy { ATGSpec.Patterns.charIntervalNumber.toStatefulRegex() }
        val charInterval by lazy { ATGSpec.Patterns.charInterval.toStatefulRegex() }
        val startCode by lazy { ATGSpec.Patterns.startCode.toStatefulRegex() }
        val endCode by lazy { ATGSpec.Patterns.endCode.toStatefulRegex() }
    }

    private val keywords = mapOf(
        "ANY" to TokenType.ANY,
        "CHARACTERS" to TokenType.CHARACTERS,
        "COMMENTS" to TokenType.COMMENTS,
        "COMPILER" to TokenType.COMPILER,
        "CONTEXT" to TokenType.CONTEXT,
        "END" to TokenType.END,
        "FROM" to TokenType.FROM,
        "IF" to TokenType.IF,
        "IGNORE" to TokenType.IGNORE,
        "IGNORECASE" to TokenType.IGNORE_CASE,
        "NESTER" to TokenType.NESTER,
        "PRAGMAS" to TokenType.PRAGMAS,
        "PRODUCTIONS" to TokenType.PRODUCTIONS,
        "SYNC" to TokenType.SYNC,
        "TO" to TokenType.TO,
        "TOKENS" to TokenType.TOKENS,
        "KEYWORDS" to TokenType.KEYWORDS,
        "WEAK" to TokenType.WEAK,
        "EXCEPT" to TokenType.EXCEPT,
    )

    private fun isAtEnd(): Boolean {
        return current >= source.length
    }

    fun scanTokens(): Collection<Token> {
        while (!isAtEnd()) {
            start = current
            scanToken()
        }

        return tokens
    }

    private fun scanToken() {
        val cur = advance()
        when(cur) {
            in ATGSpec.ignore, ' ', '\n' -> {}
            '+' -> addToken(TokenType.PLUS)
            '-' -> addToken(TokenType.MINUS)
            '=' -> addToken(TokenType.EQUALS)
            '(' -> startCode(cur)
            ')' -> addToken(TokenType.PARENTHESIS_CLOSE)
            '[' -> addToken(TokenType.BRACKET_OPEN)
            ']' -> addToken(TokenType.BRACKET_CLOSE)
            '{' -> addToken(TokenType.CURLY_BRACKET_OPEN)
            '}' -> addToken(TokenType.CURLY_BRACKET_CLOSE)
            '|' -> addToken(TokenType.PIPE)
            '.' -> endCode(cur)
            '<' -> addToken(TokenType.GT)
            '>' -> addToken(TokenType.LT)
            '\'' -> char(cur)
            'C' -> charNumber(cur)
            ATGSpec.quotes -> string(cur)
            in ATGSpec.letter -> ident(cur)
            else -> {}
        }
    }

    private fun advance(): Char {
        return source[current++]
    }

    private fun getCurrentSubString(): String = source.substring(start, current)

    private fun addToken(type: TokenType) {
        val token = Token(type, getCurrentSubString(), start, current - start)
        tokens.add(token)
    }

    private fun keyword(): TokenType? {
        return keywords[getCurrentSubString()]
    }

    private fun advance(n: Int) {
        repeat(n) { if(!isAtEnd()) advance() }
    }

    private fun matchWhilePossible(cur: Char, regex: StatefulRegex): String {
        return "$cur${source.substring(current)}".takeWhile {
            val hasNext = regex.hasNext(it)
            if(hasNext)
                regex.move(it)
            hasNext
        }
    }

    private fun ident(cur: Char) {
        val match = matchWhilePossible(cur, ident)

        advance(match.length - 1)
        ident.reset()

        val kw = keyword()

        if(kw == null)
            addToken(TokenType.IDENT)
        else
            addToken(kw)
    }

    private fun string(cur: Char) {
        val match = matchWhilePossible(cur, string)
        advance(match.length - 1)
        string.reset()
        addToken(TokenType.STRING)
    }

    private fun char(cur: Char) {
        val matchInterval = matchWhilePossible(cur, charInterval)

        if(charInterval.isAccepted()) {
            advance(matchInterval.length - 1)
            charInterval.reset()
            addToken(TokenType.CHAR_INTERVAL)
            return
        } else {
            charInterval.reset()
        }

        val match = matchWhilePossible(cur, char)

        if(char.isAccepted()) {
            advance(match.length - 1)
            char.reset()
            addToken(TokenType.CHAR)
            return
        } else {
            char.reset()
        }

    }

    private fun charNumber(cur: Char) {
        val charNumberIntervalMatch = matchWhilePossible(cur, charNumberInterval)

        if(charNumberInterval.isAccepted()) {
            advance(charNumberIntervalMatch.length - 1)
            charNumberInterval.reset()
            addToken(TokenType.CHAR_NUMBER_INTERVAL)
            return
        } else {
            charNumberInterval.reset()
        }

        val matchCharNumber = matchWhilePossible(cur, charNumber)

        if(charNumber.isAccepted()) {
            advance(matchCharNumber.length - 1)

            charNumber.reset()

            addToken(TokenType.CHAR_NUMBER)
        } else {
            charNumber.reset()
            ident(cur)
        }
    }

    private fun startCode(cur: Char) {
        val match = matchWhilePossible(cur, startCode)

        if(startCode.isAccepted()) {
            advance(match.length - 1)

            startCode.reset()

            addToken(TokenType.START_CODE)
            return
        } else {
            startCode.reset()
        }

        addToken(TokenType.PARENTHESIS_OPEN)

    }

    private fun endCode(cur: Char) {
        val match = matchWhilePossible(cur, endCode)

        if(endCode.isAccepted()) {
            advance(match.length - 1)

            endCode.reset()

            addToken(TokenType.END_CODE)
            return
        } else {
            endCode.reset()
        }

        addToken(TokenType.DOT)
    }

}
