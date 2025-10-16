package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExpressionParserAndMainTests {

    @Test
    @DisplayName("Parser: валидные выражения, отрицательные числа, идентификаторы")
    void parserValidCases() {
        Main.Expression e = Main.Expression.parseFully("(3+(2*x))");
        assertEquals(17, e.eval(Map.of("x", 7)));

        Main.Expression neg = Main.Expression.parseFully("-42");
        assertEquals(-42, neg.eval(Map.of()));

        Main.Expression id = Main.Expression.parseFully("_name1");
        assertEquals(5, id.eval(Map.of("_name1", 5)));
    }

    @Test
    @DisplayName("Parser: ошибки — неизвестный оператор, лишние символы, незакрытая скобка, '-x'")
    void parserErrorCases() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Main.Expression.parseFully("(1$2)")
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> Main.Expression.parseFully("(1+2))")
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> Main.Expression.parseFully("(1+2")
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> Main.Expression.parseFully("-x")
        );
    }

    @Test
    @DisplayName("Main.main: демонстрация печати и вычислений завершается корректно")
    void mainRunsAndPrints() throws Exception {
        PrintStream prev = System.out;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bout, true, StandardCharsets.UTF_8));
        try {
            Main.main(new String[0]);
        } finally {
            System.setOut(prev);
        }
        String out = bout.toString(StandardCharsets.UTF_8);
        assertTrue(out.contains("23"));
        assertTrue(out.contains("(3+(2*x))"));
        assertTrue(out.contains("0"));
        assertTrue(out.contains("(a+b)"));
        assertTrue(out.contains("8"));
    }
}