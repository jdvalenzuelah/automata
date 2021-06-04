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
        val startAttr by lazy { ATGSpec.Patterns.startAttr.toStatefulRegex() }
        val endAttr by lazy { ATGSpec.Patterns.endAttr.toStatefulRegex() }
    }

    private val keywords = mapOf(
        "ANY" to ATGTokenType.ANY,
        "CHARACTERS" to ATGTokenType.CHARACTERS,
        "COMMENTS" to ATGTokenType.COMMENTS,
        "COMPILER" to ATGTokenType.COMPILER,
        "CONTEXT" to ATGTokenType.CONTEXT,
        "END" to ATGTokenType.END,
        "FROM" to ATGTokenType.FROM,
        "IF" to ATGTokenType.IF,
        "IGNORE" to ATGTokenType.IGNORE,
        "IGNORECASE" to ATGTokenType.IGNORE_CASE,
        "NESTER" to ATGTokenType.NESTER,
        "PRAGMAS" to ATGTokenType.PRAGMAS,
        "PRODUCTIONS" to ATGTokenType.PRODUCTIONS,
        "SYNC" to ATGTokenType.SYNC,
        "TO" to ATGTokenType.TO,
        "TOKENS" to ATGTokenType.TOKENS,
        "KEYWORDS" to ATGTokenType.KEYWORDS,
        "WEAK" to ATGTokenType.WEAK,
        "EXCEPT" to ATGTokenType.EXCEPT,
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
            '+' -> addToken(ATGTokenType.PLUS)
            '-' -> addToken(ATGTokenType.MINUS)
            '=' -> addToken(ATGTokenType.EQUALS)
            '(' -> startCode(cur)
            ')' -> addToken(ATGTokenType.PARENTHESIS_CLOSE)
            '[' -> addToken(ATGTokenType.BRACKET_OPEN)
            ']' -> addToken(ATGTokenType.BRACKET_CLOSE)
            '{' -> addToken(ATGTokenType.CURLY_BRACKET_OPEN)
            '}' -> addToken(ATGTokenType.CURLY_BRACKET_CLOSE)
            '|' -> addToken(ATGTokenType.PIPE)
            '.' -> endCode(cur)
            '<' -> startAttr(cur)
            '>' -> addToken(ATGTokenType.LT)
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

    private fun peek(n: Int): String {
        return source.substring(current, (current + n).coerceAtMost(source.length))
    }

    private fun getCurrentSubString(): String = source.substring(start, current)

    private fun addToken(type: ATGTokenType) {
        val token = Token(type, getCurrentSubString(), start, current - start)
        tokens.add(token)
    }

    private fun keyword(): ATGTokenType? {
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
            addToken(ATGTokenType.IDENT)
        else
            addToken(kw)
    }

    private fun string(cur: Char) {
        val match = matchWhilePossible(cur, string)
        advance(match.length - 1)
        string.reset()
        addToken(ATGTokenType.STRING)
    }

    private fun char(cur: Char) {
        val matchInterval = matchWhilePossible(cur, charInterval)

        if(charInterval.isAccepted()) {
            advance(matchInterval.length - 1)
            charInterval.reset()
            addToken(ATGTokenType.CHAR_INTERVAL)
            return
        } else {
            charInterval.reset()
        }

        val match = matchWhilePossible(cur, char)

        if(char.isAccepted()) {
            advance(match.length - 1)
            char.reset()
            addToken(ATGTokenType.CHAR)
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
            addToken(ATGTokenType.CHAR_NUMBER_INTERVAL)
            return
        } else {
            charNumberInterval.reset()
        }

        val matchCharNumber = matchWhilePossible(cur, charNumber)

        if(charNumber.isAccepted()) {
            advance(matchCharNumber.length - 1)

            charNumber.reset()

            addToken(ATGTokenType.CHAR_NUMBER)
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

            addToken(ATGTokenType.START_CODE)
            start = current
            consumeCode()

            return
        } else {
            startCode.reset()
        }

        addToken(ATGTokenType.PARENTHESIS_OPEN)

    }

    private fun startAttr(cur: Char) {
        val match = matchWhilePossible(cur, startAttr)

        if(startAttr.isAccepted()) {
            advance(match.length - 1)
            startAttr.reset()
            addToken(ATGTokenType.START_ATTR)
            start = current
            consumeCode()
            return
        }

        startAttr.reset()

        addToken(ATGTokenType.GT)

    }

    private fun consumeCode() {
        val code = StringBuilder()
        while (!isAtEnd() && peek(2) != ".)" && peek(2) != ".>") {
            code.append(advance())
        }

        addToken(ATGTokenType.CODE_BLOCK)
        start = current // Ignore .

        val cur = advance()
        if(cur == '.')
            endCode(cur)
        else
            error("Unterminated code!")
    }

    private fun endCode(cur: Char) {
        val match = matchWhilePossible(cur, endCode)

        if(endCode.isAccepted()) {
            advance(match.length - 1)

            endCode.reset()

            addToken(ATGTokenType.END_CODE)
            return
        }

        endCode.reset()

        val secondMatch = matchWhilePossible(cur, endAttr)

        if(endAttr.isAccepted()) {
            advance(secondMatch.length - 1)
            endAttr.reset()
            addToken(ATGTokenType.END_ATTR)
            return
        }

        endAttr.reset()

        addToken(ATGTokenType.DOT)
    }

}
