package org.example;

import java.util.HashMap;
import java.util.Map;

import org.example.Util.Expression;

public class Binaries {
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
}
