package org.example;

import java.util.HashMap;
import java.util.Map;

/**
 * Мини-DSL для арифметических выражений:
 * парсер с полными скобками, вычисление, упрощение и производные.
 */
public class Main {

    abstract static class Expression {
        public abstract String render();

        public abstract Expression derivative(String var);

        public abstract int eval(Map<String, Integer> env);

        public abstract boolean hasVariables();

        public Expression simplify() {
            return this;
        }

        public final void print() {
            System.out.println(render());
        }

        @Override
        public final String toString() {
            return render();
        }

        public final int eval(String assignments) {
            Map<String, Integer> env = parseAssignments(assignments);
            return eval(env);
        }

        private static Map<String, Integer> parseAssignments(String s) {
            Map<String, Integer> env = new HashMap<>();
            if (s == null) {
                return env;
            }
            String[] parts = s.split(";");
            for (int idx = 0; idx < parts.length; idx++) {
                String p = parts[idx].trim();
                if (p.isEmpty()) {
                    continue;
                }
                String[] kv = p.split("=");
                if (kv.length != 2) {
                    throw new IllegalArgumentException("Неверная подстановка: " + p);
                }
                String key = kv[0].trim();
                String val = kv[1].trim();
                env.put(key, Integer.parseInt(val));
            }
            return env;
        }

        public static Expression parseFully(String src) {
            return new FullyParenParser(src).parse();
        }
    }

    static final class Number extends Expression {
        final int value;

        Number(int value) {
            this.value = value;
        }

        @Override
        public String render() {
            return Integer.toString(value);
        }

        @Override
        public Expression derivative(String var) {
            return new Number(0);
        }

        @Override
        public int eval(Map<String, Integer> env) {
            return value;
        }

        @Override
        public boolean hasVariables() {
            return false;
        }

        @Override
        public boolean equals(Object o) {
            return (o instanceof Number) && ((Number) o).value == value;
        }

        @Override
        public int hashCode() {
            return value;
        }
    }

    static final class Variable extends Expression {
        final String name;

        Variable(String name) {
            this.name = name;
        }

        @Override
        public String render() {
            return name;
        }

        @Override
        public Expression derivative(String var) {
            return new Number(name.equals(var) ? 1 : 0);
        }

        @Override
        public int eval(Map<String, Integer> env) {
            if (!env.containsKey(name)) {
                throw new IllegalArgumentException(
                        "Нет значения для переменной: " + name
                );
            }
            return env.get(name);
        }

        @Override
        public boolean hasVariables() {
            return true;
        }

        @Override
        public boolean equals(Object o) {
            return (o instanceof Variable) && ((Variable) o).name.equals(name);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }

    abstract static class Binary extends Expression {
        final Expression left;
        final Expression right;

        Binary(Expression left, Expression right) {
            this.left = left;
            this.right = right;
        }

        protected abstract char opChar();

        protected abstract int apply(int a, int b);

        @Override
        public String render() {
            return "(" + left.render() + opChar() + right.render() + ")";
        }

        @Override
        public int eval(Map<String, Integer> env) {
            return apply(left.eval(env), right.eval(env));
        }

        @Override
        public boolean hasVariables() {
            return left.hasVariables() || right.hasVariables();
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || o.getClass() != this.getClass()) {
                return false;
            }
            Binary b = (Binary) o;
            return left.equals(b.left) && right.equals(b.right);
        }

        @Override
        public int hashCode() {
            return (getClass().getName().hashCode() * 31 + left.hashCode()) * 31
                    + right.hashCode();
        }

        protected static boolean isZero(Expression e) {
            return (e instanceof Number) && ((Number) e).value == 0;
        }

        protected static boolean isOne(Expression e) {
            return (e instanceof Number) && ((Number) e).value == 1;
        }

        protected static boolean bothConst(Expression a, Expression b) {
            return !a.hasVariables() && !b.hasVariables();
        }

        protected static int evalConst(Expression e) {
            return e.eval(new HashMap<>());
        }
    }

    static final class Add extends Binary {
        Add(Expression l, Expression r) {
            super(l, r);
        }

        @Override
        protected char opChar() {
            return '+';
        }

        @Override
        protected int apply(int a, int b) {
            return a + b;
        }

        @Override
        public Expression derivative(String v) {
            return new Add(left.derivative(v), right.derivative(v));
        }

        @Override
        public Expression simplify() {
            Expression leftSimpl = left.simplify();
            Expression rightSimpl = right.simplify();
            if (bothConst(leftSimpl, rightSimpl)) {
                return new Number(evalConst(new Add(leftSimpl, rightSimpl)));
            }
            if (isZero(leftSimpl)) {
                return rightSimpl;
            }
            if (isZero(rightSimpl)) {
                return leftSimpl;
            }
            return new Add(leftSimpl, rightSimpl);
        }
    }

    static final class Sub extends Binary {
        Sub(Expression l, Expression r) {
            super(l, r);
        }

        @Override
        protected char opChar() {
            return '-';
        }

        @Override
        protected int apply(int a, int b) {
            return a - b;
        }

        @Override
        public Expression derivative(String v) {
            return new Sub(left.derivative(v), right.derivative(v));
        }

        @Override
        public Expression simplify() {
            Expression leftSimpl = left.simplify();
            Expression rightSimpl = right.simplify();
            if (leftSimpl.equals(rightSimpl)) {
                return new Number(0);
            }
            if (bothConst(leftSimpl, rightSimpl)) {
                return new Number(evalConst(new Sub(leftSimpl, rightSimpl)));
            }
            return new Sub(leftSimpl, rightSimpl);
        }
    }

    static final class Mul extends Binary {
        Mul(Expression l, Expression r) {
            super(l, r);
        }

        @Override
        protected char opChar() {
            return '*';
        }

        @Override
        protected int apply(int a, int b) {
            return a * b;
        }

        @Override
        public Expression derivative(String v) {
            return new Add(
                    new Mul(left.derivative(v), right),
                    new Mul(left, right.derivative(v))
            );
        }

        @Override
        public Expression simplify() {
            Expression leftSimpl = left.simplify();
            Expression rightSimpl = right.simplify();
            if (isZero(leftSimpl) || isZero(rightSimpl)) {
                return new Number(0);
            }
            if (isOne(leftSimpl)) {
                return rightSimpl;
            }
            if (isOne(rightSimpl)) {
                return leftSimpl;
            }
            if (bothConst(leftSimpl, rightSimpl)) {
                return new Number(evalConst(new Mul(leftSimpl, rightSimpl)));
            }
            return new Mul(leftSimpl, rightSimpl);
        }
    }

    static final class Div extends Binary {
        Div(Expression l, Expression r) {
            super(l, r);
        }

        @Override
        protected char opChar() {
            return '/';
        }

        @Override
        public int apply(int a, int b) {
            if (b == 0) {
                throw new ArithmeticException("Деление на ноль");
            }
            return a / b;
        }

        @Override
        public Expression derivative(String v) {
            return new Div(
                    new Sub(
                            new Mul(left.derivative(v), right),
                            new Mul(left, right.derivative(v))
                    ),
                    new Mul(right, right)
            );
        }

        @Override
        public Expression simplify() {
            Expression leftSimpl = left.simplify();
            Expression rightSimpl = right.simplify();
            if (bothConst(leftSimpl, rightSimpl)) {
                try {
                    return new Number(evalConst(new Div(leftSimpl, rightSimpl)));
                } catch (ArithmeticException ex) {
                    return new Div(leftSimpl, rightSimpl);
                }
            }
            if (isZero(leftSimpl)) {
                return new Number(0);
            }
            if (isOne(rightSimpl)) {
                return leftSimpl;
            }
            return new Div(leftSimpl, rightSimpl);
        }
    }

    static final class FullyParenParser {
        private final String source;
        private int index = 0;

        FullyParenParser(String s) {
            this.source = s;
        }

        Expression parse() {
            skip();
            Expression e = parseExpr();
            skip();
            if (index != source.length()) {
                throw error("Лишние символы в конце");
            }
            return e;
        }

        private Expression parseExpr() {
            skip();
            if (peek() == '(') {
                index++;
                Expression leftExpr = parseExpr();
                skip();
                char op = next();
                Expression rightExpr = parseExpr();
                skip();
                expect(')');
                return make(op, leftExpr, rightExpr);
            }
            if (peek() == '-' && isDigit(peek(1))) {
                return new Number(parseInt());
            }
            if (isDigit(peek())) {
                return new Number(parseInt());
            }
            if (isIdentStart(peek())) {
                return new Variable(parseIdent());
            }
            throw error("Ожидалось '(', число или переменная");
        }

        private Expression make(char op, Expression l, Expression r) {
            switch (op) {
                case '+':
                    return new Add(l, r);
                case '-':
                    return new Sub(l, r);
                case '*':
                    return new Mul(l, r);
                case '/':
                    return new Div(l, r);
                default:
                    throw error("Неизвестная операция: " + op);
            }
        }

        private void skip() {
            while (index < source.length()
                    && Character.isWhitespace(source.charAt(index))) {
                index++;
            }
        }

        private char peek() {
            return index < source.length() ? source.charAt(index) : '\0';
        }

        private char peek(int k) {
            int j = index + k;
            return j < source.length() ? source.charAt(j) : '\0';
        }

        private char next() {
            if (index >= source.length()) {
                throw error("Неожиданный конец строки");
            }
            return source.charAt(index++);
        }

        private void expect(char c) {
            char got = next();
            if (got != c) {
                throw error(
                        "Ожидался символ '" + c + "', а получен '" + got + "'"
                );
            }
        }

        private static boolean isDigit(char c) {
            return c >= '0' && c <= '9';
        }

        private static boolean isIdentStart(char c) {
            return Character.isLetter(c) || c == '_';
        }

        private int parseInt() {
            int sign = 1;
            if (peek() == '-') {
                sign = -1;
                index++;
            }
            int val = 0;
            if (!isDigit(peek())) {
                throw error("Ожидалось число");
            }
            while (isDigit(peek())) {
                val = val * 10 + (next() - '0');
            }
            return sign * val;
        }

        private String parseIdent() {
            if (!isIdentStart(peek())) {
                throw error("Ожидалось имя переменной");
            }
            StringBuilder sb = new StringBuilder();
            while (index < source.length()) {
                char c = source.charAt(index);
                if (Character.isLetterOrDigit(c) || c == '_') {
                    sb.append(c);
                    index++;
                } else {
                    break;
                }
            }
            return sb.toString();
        }

        private IllegalArgumentException error(String msg) {
            return new IllegalArgumentException(
                    msg + " (позиция " + index + " в \"" + source + "\")"
            );
        }
    }

    /** Точка входа для демонстрации. */
    public static void main(String[] args) {
        Expression e = new Add(
                new Number(3),
                new Mul(new Number(2), new Variable("x"))
        );
        e.print();

        Expression de = e.derivative("x");
        de.print();

        int result = e.eval("x = 10; y = 13");
        System.out.println(result);

        Expression p = Expression.parseFully("(3+(2*x))");
        p.print();

        Expression s1 = new Mul(new Number(0), new Variable("x")).simplify();
        s1.print();

        Expression s2 = new Mul(
                new Number(1),
                Expression.parseFully("(a+b)")
        ).simplify();
        s2.print();

        Expression s3 = new Sub(
                Expression.parseFully("(t*(u+v))"),
                Expression.parseFully("(t*(u+v))")
        ).simplify();
        s3.print();

        Expression s4 = Expression.parseFully("((2*3)+(10/5))").simplify();
        s4.print();
    }
}