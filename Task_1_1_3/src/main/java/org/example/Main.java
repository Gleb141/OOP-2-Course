package org.example;


import java.util.HashMap;
import java.util.Map;

public class Main {

    static abstract class Expression {
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
            Map<String, Integer> env = new HashMap<String, Integer>();
            if (s == null) return env;
            String[] parts = s.split(";");
            for (int i = 0; i < parts.length; i++) {
                String p = parts[i].trim();
                if (p.isEmpty()) continue;
                String[] kv = p.split("=");
                if (kv.length != 2) throw new IllegalArgumentException("Неверная подстановка: " + p);
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

        public String render() {
            return Integer.toString(value);
        }

        public Expression derivative(String var) {
            return new Number(0);
        }

        public int eval(Map<String, Integer> env) {
            return value;
        }

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

        public String render() {
            return name;
        }

        public Expression derivative(String var) {
            return new Number(name.equals(var) ? 1 : 0);
        }

        public int eval(Map<String, Integer> env) {
            if (!env.containsKey(name)) throw new IllegalArgumentException("Нет значения для переменной: " + name);
            return env.get(name);
        }

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

    static abstract class Binary extends Expression {
        final Expression left;
        final Expression right;

        Binary(Expression left, Expression right) {
            this.left = left;
            this.right = right;
        }

        protected abstract char opChar();

        protected abstract int apply(int a, int b);

        public String render() {
            return "(" + left.render() + opChar() + right.render() + ")";
        }

        public int eval(Map<String, Integer> env) {
            return apply(left.eval(env), right.eval(env));
        }

        public boolean hasVariables() {
            return left.hasVariables() || right.hasVariables();
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || o.getClass() != this.getClass()) return false;
            Binary b = (Binary) o;
            return left.equals(b.left) && right.equals(b.right);
        }

        @Override
        public int hashCode() {
            return (getClass().getName().hashCode() * 31 + left.hashCode()) * 31 + right.hashCode();
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
            return e.eval(new HashMap<String, Integer>());
        }
    }

    static final class Add extends Binary {
        Add(Expression l, Expression r) {
            super(l, r);
        }

        protected char opChar() {
            return '+';
        }

        protected int apply(int a, int b) {
            return a + b;
        }

        public Expression derivative(String v) {
            return new Add(left.derivative(v), right.derivative(v));
        }

        public Expression simplify() {
            Expression L = left.simplify(), R = right.simplify();
            if (bothConst(L, R)) return new Number(evalConst(new Add(L, R)));
            if (isZero(L)) return R;
            if (isZero(R)) return L;
            return new Add(L, R);
        }
    }

    static final class Sub extends Binary {
        Sub(Expression l, Expression r) {
            super(l, r);
        }

        protected char opChar() {
            return '-';
        }

        protected int apply(int a, int b) {
            return a - b;
        }

        public Expression derivative(String v) {
            return new Sub(left.derivative(v), right.derivative(v));
        }

        public Expression simplify() {
            Expression L = left.simplify(), R = right.simplify();
            if (L.equals(R)) return new Number(0);
            if (bothConst(L, R)) return new Number(evalConst(new Sub(L, R)));
            return new Sub(L, R);
        }
    }

    static final class Mul extends Binary {
        Mul(Expression l, Expression r) {
            super(l, r);
        }

        protected char opChar() {
            return '*';
        }

        protected int apply(int a, int b) {
            return a * b;
        }

        public Expression derivative(String v) {
            return new Add(new Mul(left.derivative(v), right), new Mul(left, right.derivative(v)));
        }

        public Expression simplify() {
            Expression L = left.simplify(), R = right.simplify();
            if (isZero(L) || isZero(R)) return new Number(0);
            if (isOne(L)) return R;
            if (isOne(R)) return L;
            if (bothConst(L, R)) return new Number(evalConst(new Mul(L, R)));
            return new Mul(L, R);
        }
    }

    static final class Div extends Binary {
        Div(Expression l, Expression r) {
            super(l, r);
        }

        protected char opChar() {
            return '/';
        }

        public int apply(int a, int b) {
            if (b == 0) throw new ArithmeticException("Деление на ноль");
            return a / b;
        }

        public Expression derivative(String v) {
            return new Div(new Sub(new Mul(left.derivative(v), right), new Mul(left, right.derivative(v))), new Mul(right, right));
        }

        public Expression simplify() {
            Expression L = left.simplify(), R = right.simplify();
            if (bothConst(L, R)) {
                try {
                    return new Number(evalConst(new Div(L, R)));
                } catch (ArithmeticException ex) {
                }
            }
            if (isZero(L)) return new Number(0);
            if (isOne(R)) return L;
            return new Div(L, R);
        }
    }

    static final class FullyParenParser {
        private final String s;
        private int i = 0;

        FullyParenParser(String s) {
            this.s = s;
        }

        Expression parse() {
            skip();
            Expression e = parseExpr();
            skip();
            if (i != s.length()) throw error("Лишние символы в конце");
            return e;
        }

        private Expression parseExpr() {
            skip();
            if (peek() == '(') {
                i++;
                Expression L = parseExpr();
                skip();
                char op = next();
                Expression R = parseExpr();
                skip();
                expect(')');
                return make(op, L, R);
            }
            if (peek() == '-' && isDigit(peek(1))) return new Number(parseInt());
            if (isDigit(peek())) return new Number(parseInt());
            if (isIdentStart(peek())) return new Variable(parseIdent());
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
            while (i < s.length() && Character.isWhitespace(s.charAt(i))) i++;
        }

        private char peek() {
            return i < s.length() ? s.charAt(i) : '\0';
        }

        private char peek(int k) {
            int j = i + k;
            return j < s.length() ? s.charAt(j) : '\0';
        }

        private char next() {
            if (i >= s.length()) throw error("Неожиданный конец строки");
            return s.charAt(i++);
        }

        private void expect(char c) {
            char got = next();
            if (got != c) throw error("Ожидался символ '" + c + "', а получен '" + got + "'");
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
                i++;
            }
            int val = 0;
            if (!isDigit(peek())) throw error("Ожидалось число");
            while (isDigit(peek())) val = val * 10 + (next() - '0');
            return sign * val;
        }

        private String parseIdent() {
            if (!isIdentStart(peek())) throw error("Ожидалось имя переменной");
            StringBuilder sb = new StringBuilder();
            while (i < s.length()) {
                char c = s.charAt(i);
                if (Character.isLetterOrDigit(c) || c == '_') {
                    sb.append(c);
                    i++;
                } else break;
            }
            return sb.toString();
        }

        private IllegalArgumentException error(String msg) {
            return new IllegalArgumentException(msg + " (позиция " + i + " в \"" + s + "\")");
        }
    }

    public static void main(String[] args) {
        Expression e = new Add(new Number(3), new Mul(new Number(2), new Variable("x")));
        e.print();
        Expression de = e.derivative("x");
        de.print();
        int result = e.eval("x = 10; y = 13");
        System.out.println(result);
        Expression p = Expression.parseFully("(3+(2*x))");
        p.print();
        Expression s1 = new Mul(new Number(0), new Variable("x")).simplify();
        s1.print();
        Expression s2 = new Mul(new Number(1), Expression.parseFully("(a+b)")).simplify();
        s2.print();
        Expression s3 = new Sub(Expression.parseFully("(t*(u+v))"), Expression.parseFully("(t*(u+v))")).simplify();
        s3.print();
        Expression s4 = Expression.parseFully("((2*3)+(10/5))").simplify();
        s4.print();
    }
}

