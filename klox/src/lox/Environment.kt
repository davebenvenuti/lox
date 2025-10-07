package com.craftinginterpreters.lox

class Environment {
    private val values = mutableMapOf<String, Any?>()
    val enclosing: Environment?

    constructor() {
        enclosing = null
    }

    constructor(enclosing: Environment) {
        this.enclosing = enclosing
    }

    fun define(name: String, v: Any?) {
        values[name] = v
    }

    fun get(name: Token): Any? {
        val lexeme = name.lexeme

        if (lexeme in values) {
            return values[lexeme]
        }

        if (enclosing != null) {
            return enclosing.get(name)
        }

        throw RuntimeException("Undefined variable '$lexeme'.")
    }

    fun assign(name: Token, v: Any?) {
        val lexeme = name.lexeme

        if (lexeme in values) {
            values[lexeme] = v
        }

        if (enclosing != null) {
            enclosing.assign(name, v)
            return
        }

        throw RuntimeException("Undefined variable '$lexeme'.")
    }
}
