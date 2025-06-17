package com.craftinginterpreters.lox;

class Interpreter : Expr.Visitor<Any?> {
    override fun visitLiteralExpr(expr: Expr.Literal): Any? {
        return expr.value
    }

    override fun visitGroupingExpr(expr: Expr.Grouping): Any? {
        return evaluate(expr.expression)
    }

    override fun visitUnaryExpr(expr: Expr.Unary): Any? {
        val right = evaluate(expr.right)
        return when (expr.operator.type) {
            TokenType.MINUS -> {
                checkNumberOperand(expr.operator, right)

                -(right as Double)
            }
            TokenType.BANG -> !isTruthy(right)
            else -> return null
        }
    }

    override fun visitBinaryExpr(expr: Expr.Binary): Any? {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)

        return when (expr.operator.type) {
            TokenType.GREATER -> {
                checkNumberOperands(expr.operator, left, right)

                (left as Double) > (right as Double)
            }
            TokenType.GREATER_EQUAL -> {
                checkNumberOperands(expr.operator, left, right)

                (left as Double) >= (right as Double)
            }
            TokenType.LESS -> {
                checkNumberOperands(expr.operator, left, right)

                (left as Double) < (right as Double)
            }
            TokenType.LESS_EQUAL -> {
                checkNumberOperands(expr.operator, left, right)
                (left as Double) <= (right as Double)
            }
            TokenType.MINUS -> {
                checkNumberOperands(expr.operator, left, right)

                (left as Double) - (right as Double)
            }
            TokenType.PLUS -> add(expr.operator, left, right) // Kind of a special case since it handles both numbers and strings
            TokenType.SLASH -> {
                checkNumberOperands(expr.operator, left, right)

                (left as Double) / (right as Double)
            }
            TokenType.STAR -> {
                checkNumberOperands(expr.operator, left, right)

                (left as Double) * (right as Double)
            }
            TokenType.BANG_EQUAL -> !isEqual(left, right)
            TokenType.EQUAL_EQUAL -> isEqual(left, right)
            else -> null
        }
    }

    fun interpret(expr: Expr?) {
        try {
            val result = evaluate(expr)

            println(stringify(result))
        } catch (error: RuntimeError) {
            Lox.runtimeError(error)
        }
    }

    private fun add(operator: Token, left: Any?, right: Any?): Any {
        if (left is Double && right is Double) {
            return left + right
        }
        if (left is String && right is String) {
            return left + right
        }
        throw RuntimeError(operator, "Operands must be two numbers or two strings.")
    }

    private fun evaluate(expr: Expr?): Any? {
        return expr?.accept(this)
    }

    private fun isTruthy(value: Any?): Boolean {
        return when (value) {
            null -> false
            is Boolean -> value
            else -> true
        }
    }

    private fun isEqual(left: Any?, right: Any?): Boolean {
        if (left == null && right == null) return true
        if (left == null || right == null) return false
        return left == right
    }

    private fun checkNumberOperand(operator: Token, operand: Any?) {
        if (operand is Double) return

        throw RuntimeError(operator, "Operand must be a number.")
    }

    private fun checkNumberOperands(operator: Token, left: Any?, right: Any?) {
        if (left is Double && right is Double) return;

        throw RuntimeError(operator, "Operands must be numbers.")
    }

    private fun stringify(obj: Any?): String {
        if (obj == null) return "nil"
        if (obj is Double) {
            // Convert to string and remove trailing .0 for whole numbers
            val text = obj.toString()
            return if (text.endsWith(".0")) text.substring(0, text.length - 2) else text
        }
        return obj.toString()
    }
}
