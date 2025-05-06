package com.craftinginterpreters.lox

import com.craftinginterpreters.lox.TokenType.*

class Parser {
    val tokens: List<Token>
    var current = 0

    private class ParseError(val token: Token, message: String) : RuntimeException(message)

    constructor(tokens: List<Token>) {
        this.tokens = tokens
    }

    fun parse(): Expr? {
        try {
            return expression()
        } catch (error: ParseError) {
            return null
        }
    }

    private fun expression(): Expr {
        return equality()
    }

    private fun equality(): Expr {
        var expr = comparison()

        while(match(BANG_EQUAL, EQUAL_EQUAL)) {
            val op = previous()
            val right = comparison()
            expr = Expr.Binary(expr, op, right)
        }

        return expr
    }

    private fun comparison(): Expr {
        var expr = term()

        while(match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            val op = previous()
            val right = term()
            expr = Expr.Binary(expr, op, right)
        }

        return expr
    }

    private fun term(): Expr {
        var expr = factor()

        while(match(MINUS, PLUS)) {
            val op = previous()
            val right = factor()
            expr = Expr.Binary(expr, op, right)
        }

        return expr
    }

    private fun factor(): Expr {
        var expr = unary()

        while(match(SLASH, STAR)) {
            val op = previous()
            val right = unary()

            expr = Expr.Binary(expr, op, right)
        }

        return expr
    }

    private fun unary(): Expr {
        if(match(BANG, MINUS)) {
            val op = previous()
            val right = unary()
            return Expr.Unary(op, right)
        }

        return primary()
    }

    private fun primary(): Expr {
        if(match(FALSE)) return Expr.Literal(false)
        if(match(TRUE)) return Expr.Literal(true)
        if(match(NIL)) return Expr.Literal(null)

        if(match(NUMBER, STRING)) {
            return Expr.Literal(previous().literal)
        }

        if(match(LEFT_PAREN)) {
            val expr = expression()
            consume(RIGHT_PAREN, "Expect ')' after expression.")

            return Expr.Grouping(expr)
        }

        throw error(peek(), "Expect expression.")
    }

    private fun match(vararg types: TokenType): Boolean {
        for (type in types) {
            if (check(type)) {
                advance()
                return true
            }
        }
        return false
    }

    private fun check(type: TokenType): Boolean {
        if(isAtEnd()) return false
        return peek().type == type
    }

    private fun advance(): Token {
        if (!isAtEnd()) current++

        return previous()
    }

    private fun isAtEnd(): Boolean = peek().type == TokenType.EOF

    private fun peek(): Token = tokens[current]

    private fun previous(): Token = tokens[current - 1]

    private fun consume(type: TokenType, message: String): Token {
        if (check(type)) return advance()

        throw error(peek(), message)
    }

    private fun error(token: Token, message: String): ParseError {
        Lox.error(token, message)

        return ParseError(token, message)
    }

    private fun synchronize() {
        advance()

        while(!isAtEnd()) {
            if(previous().type == SEMICOLON) return

            when(peek().type) {
                CLASS, FUN, VAR, FOR, IF, WHILE, PRINT, RETURN -> return
                else -> { /* noop */ }
            }

            advance()
        }
    }
}
