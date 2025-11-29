package org.example;

public class FullParenParser {

    static final class FullyParenParser {
        private final String source;
        private int index = 0;

        FullyParenParser(String s) {
            this.source = s;
        }

        Main.Expression parse() {
            skip();
            Main.Expression e = parseExpr();
            skip();
            if (index != source.length()) {
                throw error("Лишние символы в конце");
            }
            return e;
        }

        private Main.Expression parseExpr() {
            skip();
            if (peek() == '(') {
                index++;
                final Main.Expression leftExpr = parseExpr();
                skip();
                char op = next();
                Main.Expression rightExpr = parseExpr();
                skip();
                expect(')');
                return make(op, leftExpr, rightExpr);
            }
            if (peek() == '-' && isDigit(peek(1))) {
                return new Main.Number(parseInt());
            }
            if (isDigit(peek())) {
                return new Main.Number(parseInt());
            }
            if (isIdentStart(peek())) {
                return new Main.Variable(parseIdent());
            }
            throw error("Ожидалось '(', число или переменная");
        }

        private Main.Expression make(char op, Main.Expression l, Main.Expression r) {
            switch (op) {
                case '+':
                    return new Main.Add(l, r);
                case '-':
                    return new Main.Sub(l, r);
                case '*':
                    return new Main.Mul(l, r);
                case '/':
                    return new Main.Div(l, r);
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
}
