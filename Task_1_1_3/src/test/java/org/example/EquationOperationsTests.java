package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for EquationOperationsTests.
 */
public class EquationOperationsTests {

    @Test
    @DisplayName("Создание выражения")
    void equationCreate() {
        Main.Expression e = new Main.Add(
                new Main.Number(3),
                new Main.Mul(new Main.Number(2), new Main.Variable("x"))
        );
        assertEquals("(3+(2*x))", e.render());
    }

    @Test
    @DisplayName("Создание производной (упрощение констант)")
    void derivativeCreate() {
        Main.Expression s4 = Main.Expression.parseFully("((2*3)+(10/5))").simplify();
        assertEquals("8", s4.render());
    }

    @Test
    @DisplayName("Умножение: 0 * x -> 0")
    void multiplication() {
        Main.Expression s1 = new Main.Mul(new Main.Number(0), new Main.Variable("x")).simplify();
        assertEquals("0", s1.render());
    }

    @Test
    @DisplayName("Деление на ноль — ArithmeticException")
    void zeroDivision() {
        Throwable exception = assertThrows(
                ArithmeticException.class,
                () -> new Main.Div(
                        Main.Expression.parseFully("5"),
                        Main.Expression.parseFully("5")
                ).apply(1, 0)
        );
        assertEquals("Деление на ноль", exception.getMessage());
    }

    @Test
    @DisplayName("Пустая строка парсера — ожидается '(', число или переменная")
    void expectedBracket() {
        Throwable exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Main.Div(
                        Main.Expression.parseFully(""),
                        Main.Expression.parseFully("")
                ).apply(1, 0)
        );
        assertEquals("Ожидалось '(', число или переменная (позиция 0 в \"\")",
                exception.getMessage());
    }
}