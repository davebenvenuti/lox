package com.craftinginterpreters.lox

import java.nio.charset.Charset
import java.io.File

object Lox {
    var hadError = false

    @JvmStatic
    fun main(args: Array<String>) {
        when {
            args.size > 1 -> {
                println("Usage: jlox [script]")
                System.exit(64)
            }
            args.size == 1 -> runFile(args[0])
            else -> runPrompt()
        }
    }

    fun runPrompt() {
        while (true) {
            print("> ");
            val line = readlnOrNull() ?: break

            run(line)

            hadError = false
        }
    }

    fun runFile(path: String) {
        val content: String = File(path).readText(Charset.defaultCharset())

        run(content)

        if (hadError) System.exit(65)
    }

    private fun run(source: String) {
        val scanner = Scanner(source)
        val tokens = scanner.scanTokens()

        for (token in tokens) {
            println(token)
        }
    }

    fun error(line: Int, message: String) {
        report(line, "", message)
    }

    fun report(line: Int, where: String, message: String) {
        System.err.println("[line $line] Error $where: $message")
        hadError = true
    }
}
