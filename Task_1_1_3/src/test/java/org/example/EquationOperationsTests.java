package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Тесты базовых операций выражений.
 */
public class EquationOperationsTests {

    @Test
    @DisplayName("Создание выражения")
    void equationCreate() {
        Main.Expression expr = new Main.Add(
                new Main.Number(3),
                new Main.Mul(new Main.Number(2), new Main.Variable("x"))
        );
        assertEquals("(3+(2*x))", expr.render());
    }

    @Test
    @DisplayName("Создание производной (упрощение конкретного примера)")
    void derivativeCreate() {
        Main.Expression simplified = Main.Expression
                .parseFully("((2*3)+(10/5))")
                .simplify();
        assertEquals("8", simplified.render());
    }

    @Test
    @DisplayName("Умножение с нулём упрощается в 0")
    void multiplication() {
        Main.Expression simplified = new Main.Mul(
                new Main.Number(0),
                new Main.Variable("x")
        ).simplify();
        assertEquals("0", simplified.render());
    }

    @Test
    @DisplayName("Деление на ноль бросает ArithmeticException")
    void zeroDivision() {
        Throwable ex = assertThrows(
                ArithmeticException.class,
                () -> new Main.Div(
                        Main.Expression.parseFully("5"),
                        Main.Expression.parseFully("5")
                ).apply(1, 0)
        );
        assertEquals("Деление на ноль", ex.getMessage());
    }

    @Test
    @DisplayName("Пустая строка парсера — сообщение об ожидании выражения")
    void expectedBracket() {
        Throwable ex = assertThrows(
                IllegalArgumentException.class,
                () -> new Main.Div(
                        Main.Expression.parseFully(""),
                        Main.Expression.parseFully("")
                ).apply(1, 0)
        );
        assertEquals(
                "Ожидалось '(', число или переменная (позиция 0 в \"\")",
                ex.getMessage()
        );
    }
}