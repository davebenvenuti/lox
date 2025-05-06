package com.craftinginterpreters.lox

import java.nio.charset.Charset
import java.io.File

object Lox {
    var hadError = false

    @JvmStatic
    fun main(args: Array<String>) {
        if (args.isEmpty()) {
            runPrompt()

            return
        }

        // Process arguments
        var printAst = false
        var inputFile: String? = null

        var i = 0
        while (i < args.size) {
            when (args[i]) {
                "-h", "--help" -> {
                    printUsage()
                    return
                }
                "-p", "--print-ast" -> {
                    printAst = true
                }
                else -> {
                    if (inputFile == null) {
                        inputFile = args[i]
                    } else {
                        println("Unknown argument: ${args[i]}")
                        printUsage()
                        return
                    }
                }
            }
            i++
        }

        if(printAst) {
            astPrinter()
        }

        if (!inputFile.isNullOrEmpty()) {
            runFile(inputFile)
        } else {
            runPrompt()
        }

    }

    private fun printUsage() {
        println("""
                Usage: lox [options]
                Options:
                  -h, --help     Show this help message
                  -p, --print-ast  Enable verbose output
                """.trimIndent())
    }

    private fun astPrinter() {
        val expression = Expr.Binary(
            Expr.Unary(
                Token(TokenType.MINUS, "-", null, 1),
                Expr.Literal(123)
            ),
            Token(TokenType.STAR, "*", null, 1),
            Expr.Grouping(
                Expr.Literal(45.67)
            )
        )

        println(AstPrinter().print(expression))
    }

    private fun runPrompt() {
        while (true) {
            print("> ");
            val line = readlnOrNull() ?: break

            run(line)

            hadError = false
        }
    }

    private fun runFile(path: String) {
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
