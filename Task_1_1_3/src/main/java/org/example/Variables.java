package org.example;

import java.util.Map;

import org.example.Util.Expression;

public class Variables {
    final class Variable extends Expression {
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
}
