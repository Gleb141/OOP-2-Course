package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class EquationOperationsTests {
    @Test
    @DisplayName("Создание выражения")
    void EquationCreate() {
        Main.Expression e = new Main.Add(new Main.Number(3), new Main.Mul(new Main.Number(2), new Main.Variable("x")));
        assertEquals("(3+(2*x))", e.render());
    }

    @Test
    @DisplayName("Создание производной")
    void DerivativeCreate() {
        Main.Expression s4 = Main.Expression.parseFully("((2*3)+(10/5))").simplify();
        assertEquals("8", s4.render());
    }

    @Test
    @DisplayName("Умножение")
    void Multiplication() {
        Main.Expression s1 = new Main.Mul(new Main.Number(0), new Main.Variable("x")).simplify();
        assertEquals("0", s1.render());
    }

//    @Test
//    @DisplayName("Создание производной")
//    void zeroDivision(){
//        Main.Expression e = Main.Expression.parseFully("((2*3)+(10/0))").simplify();
//        Throwable exception = assertThrows(ArithmeticException.class,() -> {
//            if (b == 0) throw new ArithmeticException("Деление на ноль");
//            return a / b;
//        });
//        assertEquals("Деление на ноль", exception.getMessage());
//    }

    @Test
    void zeroDivision() {

        Throwable exception = assertThrows(
                ArithmeticException.class,
                () ->
                        new Main.Div(
                                Main.Expression.parseFully("5"),
                                Main.Expression.parseFully("5"))
                                .apply(1, 0)

        );
        assertEquals("Деление на ноль", exception.getMessage());
    }

    @Test
    void expectedBracket() {

        Throwable exception = assertThrows(
                IllegalArgumentException.class,
                () ->
                        new Main.Div(
                                Main.Expression.parseFully(""),
                                Main.Expression.parseFully(""))
                                .apply(1, 0)

        );
        assertEquals("Ожидалось '(', число или переменная (позиция 0 в \"\")", exception.getMessage());
    }

//    @Test
//    void expectedNumber() {
//
//        Throwable exception = assertThrows(
//                IllegalArgumentException.class,
//                () ->
//                        new Main.Div(
//                                Main.Expression.parseFully(""),
//                                Main.Expression.parseFully(""))
//                                .apply(1, 0)
//
//        );
//        assertEquals("Нет значения для переменной: ", exception.getMessage());
//    }
}
