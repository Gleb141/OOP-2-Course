package org.example;

import java.util.HashMap;
import java.util.Map;

abstract class Expression {
    public abstract String render();

    public abstract Expression derivative(String var);

    public abstract int eval(Map<String, Integer> env);

    public final int eval(String assignments) {
        Map<String, Integer> env = parseAssignments(assignments);
        return eval(env);
    }

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

    public static class IncorrectSubstitutionException extends Exception {
        public IncorrectSubstitutionException(String errmessage) {
            super(errmessage);
        }
    }

    private static void kvcheck(String p, String[] kv) throws Expression.IncorrectSubstitutionException {
        if (kv.length != 2) {
            throw new Expression.IncorrectSubstitutionException("Неверная подстановка: " + p);
        }
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
            String key = kv[0].trim();
            String val = kv[1].trim();
            env.put(key, Integer.parseInt(val));
            try {
                kvcheck(parts[idx].trim(), p.split("="));
            } catch (Expression.IncorrectSubstitutionException e) {
                System.out.println(e.getMessage());

            }
        }

        return env;

    }


    public static Expression parseFully(String src) {
        return new FullParenParser(src).parse();
    }
}
