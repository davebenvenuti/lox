package com.craftinginterpreters.lox

import com.craftinginterpreters.lox.TokenType.*

class Scanner(private val source: String) {
    private val NULL_CHARACTER = '\u0000'

    private val tokens = mutableListOf<Token>()

    private val keywords = mapOf(
        "and" to AND,
        "class" to CLASS,
        "else" to ELSE,
        "false" to FALSE,
        "for" to FOR,
        "fun" to FUN,
        "if" to IF,
        "nil" to NIL,
        "or" to OR,
        "print" to PRINT,
        "return" to RETURN,
        "super" to SUPER,
        "this" to THIS,
        "true" to TRUE,
        "var" to VAR,
        "while" to WHILE
    )

    private var start: Int = 0
    private var current: Int = 0
    private var line: Int = 1

    fun scanTokens(): List<Token> {
        while(!isAtEnd()) {
            start = current
            scanToken()
        }

        tokens.add(Token(EOF, "", null, line))

        return tokens
    }

    private fun isAtEnd(): Boolean = current >= source.length

    private fun advance(): Char = source[current++]

    private fun scanToken() {
        val c = advance()

        when(c) {
            '(' -> addToken(LEFT_PAREN)
            ')' -> addToken(RIGHT_PAREN)
            '{' -> addToken(LEFT_BRACE)
            '}' -> addToken(RIGHT_BRACE)
            ',' -> addToken(COMMA)
            '.' -> addToken(DOT)
            '-' -> addToken(MINUS)
            '+' -> addToken(PLUS)
            ';' -> addToken(SEMICOLON)
            '*' -> addToken(STAR)
            '!' -> addToken(if(match('=')) BANG_EQUAL else BANG)
            '=' -> addToken(if(match('=')) EQUAL_EQUAL else EQUAL)
            '<' -> addToken(if(match('=')) LESS_EQUAL else LESS)
            '>' -> addToken(if(match('=')) GREATER_EQUAL else GREATER)
            // "//" is a comment
            '/' -> if(match('/')) advanceUntilEndOfLine() else addToken(SLASH)
            ' ', '\r', '\t' -> { /* Ignore whitespace */ }
            '\n' -> line++
            '"' -> string()
            else -> {
                if(isDigit(c)) {
                    number()
                } else if(isAlpha(c)) {
                    identifier()
                } else {
                    Lox.error(line, "Unexpected character.")
                }
            }
        }
    }

    private fun addToken(type: TokenType) {
        addToken(type, null)
    }

    private fun addToken(type: TokenType, literal: Any?) {
        val text: String = source.substring(start, current)
        tokens.add(Token(type, text, literal, line))
    }

    private fun match(expected: Char): Boolean {
        if (isAtEnd()) return false
        if (source[current] != expected) return false

        current++
        return true
    }

    private fun peek(): Char = if(isAtEnd()) NULL_CHARACTER else source[current]

    private fun advanceUntilEndOfLine() {
        while(peek() != '\n' && !isAtEnd()) advance()
    }

    private fun string() {
        while(peek() != '"' && !isAtEnd()) {
            if(peek() == '\n') line++ // We support multi-line strings
            advance()
        }

        if(isAtEnd()) {
            Lox.error(line, "Unterminated string.")
            return
        }

        advance() // This grabs the closing '"'

        addToken(STRING, source.substring(start + 1, current - 1))
    }

    private fun isDigit(c: Char): Boolean = c in '0'..'9'

    private fun number() {
        while(isDigit(peek())) advance()

        if(peek() == '.' && isDigit(peekNext())) {
            // Consume the decimal
            advance()

            while(isDigit(peek())) advance()
        }

        addToken(NUMBER, source.substring(start, current).toDouble())
    }

    private fun peekNext(): Char {
        if(current + 1 >= source.length) return NULL_CHARACTER else return source[current + 1]
    }

    private fun isAlpha(c: Char): Boolean = c in 'a'..'z' || c in 'A'..'Z' || c == '_'

    private fun isAlphaNumeric(c: Char): Boolean = isAlpha(c) || isDigit(c)

    private fun identifier() {
        while(isAlphaNumeric(peek())) advance()

        val text: String = source.substring(start, current)
        addToken(keywords[text] ?: IDENTIFIER)
    }
}
