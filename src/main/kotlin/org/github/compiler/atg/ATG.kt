package org.github.compiler.atg

import org.github.compiler.regularExpressions.Regex
import org.github.compiler.regularExpressions.regex.tokenize.escape

enum class TokenType {
    //Single char
    PLUS,
    MINUS,
    EQUALS,
    PIPE,
    DOT,
    GT,
    LT,
    PARENTHESIS_OPEN,
    PARENTHESIS_CLOSE,
    BRACKET_OPEN,
    BRACKET_CLOSE,
    CURLY_BRACKET_OPEN,
    CURLY_BRACKET_CLOSE,

    //One or two character tokens.
    IDENT,
    STRING,
    CHAR,
    CHAR_NUMBER,
    CHAR_INTERVAL,
    NON_TOKEN,
    START_CODE,
    END_CODE,

    //KEYWORDS
    ANY,
    CHARACTERS,
    COMMENTS,
    COMPILER,
    CONTEXT,
    END,
    FROM,
    IF,
    IGNORE,
    IGNORECASE,
    NESTER,
    PRAGMAS,
    PRODUCTIONS,
    SYNC,
    TO,
    TOKENS,
    KEYWORDS,
    WEAK
}

data class Token(
    val type: TokenType,
    val lexeme: String,
    val posStart: Int,
    val length: Int
)

object ATG {
    private val ANY = (0..255).map { it.toChar() }.joinToString(separator = "")
    const val letter= "ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz"
    const val digit= "0123456789"
    private val cr = 13.toChar().toString()
    private val lf = 10.toChar().toString()
    private val tab = 9.toChar().toString()
    val ignore = cr + lf + tab
    val quotes = 34.toChar()
    val stringLetter = ANY.toList() - quotes.toString().toList() - ignore.toList()
    private val operators = "+-=()[]{}|.<>"
    val myANY = ANY.toList() - operators.toList()

    object Patterns {
        private val letter = ATG.letter.toList().joinToString(separator = "|")
        private val digit = ATG.digit.toList().joinToString(separator = "|")
        private val stringLetter = ATG.stringLetter.toList().joinToString(separator = "|") { Regex.escape(it.toString()) }
        private val myAny = ATG.myANY.toList().joinToString(separator = "|")
        val ident = "($letter)((($letter)|($digit))*)"
        val string = "($quotes)($stringLetter)($stringLetter)*($quotes)" //TODO: Fix to correct regex
        val char = "'/?($letter)'"
        val charNumber = "CHR\\((($digit)+)\\)"
        val charInterval = "($charNumber)..(${charNumber})"
        val nonToken: String = "($myAny)"
        const val startCode = "\\(."
        const val endCode = ".\\)"
    }

}
