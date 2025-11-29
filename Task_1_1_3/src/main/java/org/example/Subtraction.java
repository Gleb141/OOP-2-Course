package org.example;

public class Subtraction {
    static final class Sub extends Main.Binary {
        Sub(Main.Expression l, Main.Expression r) {
            super(l, r);
        }

        @Override
        protected char opChar() {
            return '-';
        }

        @Override
        protected int apply(int a, int b) {
            return a - b;
        }

        @Override
        public Main.Expression derivative(String v) {
            return new Main.Sub(left.derivative(v), right.derivative(v));
        }

        @Override
        public Main.Expression simplify() {
            Main.Expression leftSimpl = left.simplify();
            Main.Expression rightSimpl = right.simplify();
            if (leftSimpl.equals(rightSimpl)) {
                return new Main.Number(0);
            }
            if (bothConst(leftSimpl, rightSimpl)) {
                return new Main.Number(evalConst(new Main.Sub(leftSimpl, rightSimpl)));
            }
            return new Main.Sub(leftSimpl, rightSimpl);
        }
    }
}
