package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExpressionCoverageTests {

    private Main.Number num(int v) {
        return new Main.Number(v);
    }

    private Main.Variable var(String n) {
        return new Main.Variable(n);
    }

    @Test
    @DisplayName("Number/Variable: render, toString, eval, hasVariables, equals/hashCode, print")
    void numberVariableBasics() {
        Main.Expression numExpr = num(7);
        assertEquals("7", numExpr.render());
        assertEquals("7", numExpr.toString());
        assertEquals(7, numExpr.eval(new HashMap<>()));
        assertFalse(numExpr.hasVariables());

        Main.Expression varExpr = var("x");
        assertEquals("x", varExpr.render());
        assertEquals("x", varExpr.toString());
        Map<String, Integer> env = new HashMap<>();
        env.put("x", 42);
        assertEquals(42, varExpr.eval(env));
        assertTrue(varExpr.hasVariables());

        assertEquals(num(7), new Main.Number(7));
        assertEquals(num(7).hashCode(), new Main.Number(7).hashCode());
        assertEquals(var("x"), new Main.Variable("x"));
        assertEquals(var("x").hashCode(), new Main.Variable("x").hashCode());

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        PrintStream prev = System.out;
        System.setOut(new PrintStream(bout, true, StandardCharsets.UTF_8));
        try {
            numExpr.print();
            varExpr.print();
        } finally {
            System.setOut(prev);
        }
        String out = bout.toString(StandardCharsets.UTF_8);
        assertTrue(out.contains("7"));
        assertTrue(out.contains("x"));
    }

    @Test
    @DisplayName("Variable.eval: отсутствие значения → IllegalArgumentException")
    void variableMissingValueThrows() {
        assertThrows(IllegalArgumentException.class, () -> var("y").eval(new HashMap<>()));
    }

    @Test
    @DisplayName("Expression.eval(String): парсинг подстановок, пустые/пробельные и ошибки")
    void evalStringAssignments() {
        Main.Expression e = new Main.Add(var("x"), var("y"));
        assertEquals(3, e.eval("x=1; y=2"));
        assertEquals(3, e.eval(" x = 1 ;  ; y = 2 "));
        assertEquals(5, num(5).eval((String) null));
        assertThrows(IllegalArgumentException.class, () -> e.eval("x=1; y"));
        assertThrows(NumberFormatException.class, () -> e.eval("x = a"));
    }

    @Test
    @DisplayName("Derivatives: суммa и правило произведения; по отсутствующей переменной → 0")
    void derivativesEvaluateCorrectly() {
        Main.Expression e = new Main.Add(num(3), new Main.Mul(num(2), var("x")));
        Main.Expression dx = e.derivative("x");
        Map<String, Integer> env = Map.of("x", 5);
        assertEquals(2, dx.eval(env));

        Main.Expression dy = e.derivative("y");
        assertEquals(0, dy.eval(env));
    }

    @Test
    @DisplayName("Binary: equals по типу, hashCode стабилен")
    void binaryEqualsAndHashCode() {
        Main.Expression a = num(1);
        Main.Expression b = num(2);
        Main.Add add1 = new Main.Add(a, b);
        Main.Add add2 = new Main.Add(num(1), num(2));
        Main.Sub sub = new Main.Sub(a, b);
        assertEquals(add1, add2);
        assertNotEquals(add1, sub);
        assertEquals(add1.hashCode(), add2.hashCode());
    }

    @Test
    @DisplayName("Add: render/apply/simplify (0+X, X+0, константы)")
    void addSimplify() {
        Main.Add a1 = new Main.Add(num(0), var("x"));
        assertEquals("x", a1.simplify().render());

        Main.Add a2 = new Main.Add(var("x"), num(0));
        assertEquals("x", a2.simplify().render());

        Main.Add a3 = new Main.Add(num(2), num(3));
        assertEquals("5", a3.simplify().render());

        assertEquals("(1+2)", new Main.Add(num(1), num(2)).render());
        assertEquals(7, new Main.Add(num(3), num(4)).eval(new HashMap<>()));
    }

    @Test
    @DisplayName("Sub: simplify (X−X=0, константы), render/apply")
    void subSimplify() {
        Main.Sub s1 = new Main.Sub(var("t"), var("t"));
        assertEquals("0", s1.simplify().render());

        Main.Sub s2 = new Main.Sub(num(10), num(3));
        assertEquals("7", s2.simplify().render());

        assertEquals("(5-2)", new Main.Sub(num(5), num(2)).render());
        assertEquals(3, new Main.Sub(num(5), num(2)).eval(new HashMap<>()));
    }

    @Test
    @DisplayName("Mul: simplify (0*X=0, 1*X=X, константы), render/apply")
    void mulSimplify() {
        assertEquals("0", new Main.Mul(num(0), var("x")).simplify().render());
        assertEquals("y", new Main.Mul(num(1), var("y")).simplify().render());
        assertEquals("z", new Main.Mul(var("z"), num(1)).simplify().render());
        assertEquals("8", new Main.Mul(num(2), num(4)).simplify().render());
        assertEquals("(3*4)", new Main.Mul(num(3), num(4)).render());
        assertEquals(12, new Main.Mul(num(3), num(4)).eval(new HashMap<>()));
    }

    @Test
    @DisplayName("Div: apply, simplify (0/X=0, X/1=X, константы), деление на 0")
    void divSimplifyAndDivideByZero() {
        assertEquals("0", new Main.Div(num(0), var("x")).simplify().render());
        assertEquals("x", new Main.Div(var("x"), num(1)).simplify().render());
        assertEquals("2", new Main.Div(num(6), num(3)).simplify().render());

        Main.Expression e = new Main.Div(num(1), num(0)).simplify();
        assertTrue(e instanceof Main.Div);

        assertThrows(
                ArithmeticException.class,
                () -> new Main.Div(num(1), num(0))
                        .eval(new HashMap<>())
        );
    }
}