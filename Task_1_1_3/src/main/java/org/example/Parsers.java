package org.example;

import java.util.Map;

import org.example.Expression;

public class Parsers {
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
}
