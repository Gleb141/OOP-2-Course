package org.example;

public class Division {
    static final class Div extends Main.Binary {
        Div(Main.Expression l, Main.Expression r) {
            super(l, r);
        }

        @Override
        protected char opChar() {
            return '/';
        }

        @Override
        public int apply(int a, int b) {
            if (b == 0) {
                throw new ArithmeticException("Деление на ноль");
            }
            return a / b;
        }

        @Override
        public Main.Expression derivative(String v) {
            return new Main.Div(
                    new Main.Sub(
                            new Main.Mul(left.derivative(v), right),
                            new Main.Mul(left, right.derivative(v))
                    ),
                    new Main.Mul(right, right)
            );
        }

        @Override
        public Main.Expression simplify() {
            Main.Expression leftSimpl = left.simplify();
            Main.Expression rightSimpl = right.simplify();
            if (bothConst(leftSimpl, rightSimpl)) {
                try {
                    return new Main.Number(evalConst(new Main.Div(leftSimpl, rightSimpl)));
                } catch (ArithmeticException ex) {
                    return new Main.Div(leftSimpl, rightSimpl);
                }
            }
            if (isZero(leftSimpl)) {
                return new Main.Number(0);
            }
            if (isOne(rightSimpl)) {
                return leftSimpl;
            }
            return new Main.Div(leftSimpl, rightSimpl);
        }
    }
}
