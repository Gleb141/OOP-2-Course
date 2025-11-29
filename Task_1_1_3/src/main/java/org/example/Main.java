package org.example;

import java.util.HashMap;
import java.util.Map;

/**
 * Мини-DSL для арифметических выражений:
 * парсер с полными скобками, вычисление, упрощение и производные.
 */
public class Main {



    /** Точка входа для демонстрации. */
    public static void main(String[] args) {
//        try {
//            Expression e = new Add(new Number(3), new Mul(new Number(2), new Variable("x")));
//            int result = e.eval("x = 10; y = 13");
//            System.out.println(result);
//        } catch (Expression.IncorrectSubstitutionException ex) {
//            System.out.println("Ошибка подстановки: " + ex.getMessage());
//        }
        Expression e = new Add(
                new Number(3),
                new Mul(new Number(2), new Variable("x"))
        );
        e.print();

        Expression de = e.derivative("x");
        de.print();

        int result = e.eval("x = 10 = 20");
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