package org.github.compiler.atg

import org.github.compiler.regularExpressions.Regex
import org.github.compiler.regularExpressions.regex.tokenize.escape
import org.github.compiler.regularExpressions.regexImpl.toRegex
import org.github.compiler.regularExpressions.regexImpl.toStatefulRegex

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
    CHAR_NUMBER_INTERVAL,
    CHAR_INTERVAL,
    NON_TOKEN,
    START_CODE,
    END_CODE,
    START_ATTR,
    END_ATTR,

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
    IGNORE_CASE,
    NESTER,
    PRAGMAS,
    PRODUCTIONS,
    SYNC,
    TO,
    TOKENS,
    KEYWORDS,
    WEAK,
    EXCEPT,

    // Especial tokens
    CODE_BLOCK
}

data class Token(
    val type: TokenType,
    val lexeme: String,
    val posStart: Int,
    val length: Int
)

//TODO: Most likely missing something
object ATGSpec {
    val ANY = (0..255).map { it.toChar() }.joinToString(separator = "")
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
        private val letter = ATGSpec.letter.toList().joinToString(separator = "|")
        private val digit = ATGSpec.digit.toList().joinToString(separator = "|")
        private val stringLetter = ATGSpec.stringLetter.toList().joinToString(separator = "|") { Regex.escape(it.toString()) }
        private val myAny = ATGSpec.myANY.toList().joinToString(separator = "|")
        private val stringVal = (ATGSpec.ANY.toList() - listOf('"')).joinToString(separator = "|") { Regex.escape(it.toString()) }
        private val charVal = (ATGSpec.ANY.toList() - listOf('\'')).joinToString(separator = "|") { Regex.escape(it.toString()) }
        val ident = "($letter)((($letter)|($digit))*)"
        val string = "($quotes)($stringVal)+($quotes)" //TODO: Fix to correct regex
        val char = "'/?($charVal)'"
        val charNumber = "CHR\\((($digit)+)\\)"
        val charIntervalNumber = "($charNumber)..(${charNumber})"
        val charInterval = "'($charVal)'..'($charVal)'"
        val nonToken: String = "($myAny)"
        const val startCode = "\\(."
        const val endCode = ".\\)"
        const val startAttr = "<."
        const val endAttr = ".>"
    }

}
