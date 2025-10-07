package com.craftinginterpreters.lox

class RuntimeError(val token: Token, message: String) : Exception(message)
