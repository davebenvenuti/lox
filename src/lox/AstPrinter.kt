package com.craftinginterpreters.lox

class AstPrinter : Expr.Visitor<String> {
    fun print(expr: Expr): String {
        return expr.accept(this)
    }

    override fun visitBinaryExpr(binary: Expr.Binary): String {
        return parenthesize(binary.operator.lexeme, binary.left, binary.right)
    }

    override fun visitGroupingExpr(grouping: Expr.Grouping): String {
        return parenthesize("group", grouping.expression)
    }

    override fun visitLiteralExpr(literal: Expr.Literal): String {
        return (if (literal.value == null) "nil" else literal.value.toString())
    }

    override fun visitUnaryExpr(unary: Expr.Unary): String {
        return parenthesize(unary.operator.lexeme, unary.right)
    }

    private fun parenthesize(name: String, vararg exprs: Expr): String {
        return "($name ${exprs.joinToString(" ") { it.accept(this) }})"
    }
}
