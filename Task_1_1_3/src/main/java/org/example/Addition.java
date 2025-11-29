package org.example;

public class Addition {
    static final class Add extends Main.Binary {
        Add(Main.Expression l, Main.Expression r) {
            super(l, r);
        }

        @Override
        protected char opChar() {
            return '+';
        }

        @Override
        protected int apply(int a, int b) {
            return a + b;
        }

        @Override
        public Main.Expression derivative(String v) {
            return new Main.Add(left.derivative(v), right.derivative(v));
        }

        @Override
        public Main.Expression simplify() {
            Main.Expression leftSimpl = left.simplify();
            Main.Expression rightSimpl = right.simplify();
            if (bothConst(leftSimpl, rightSimpl)) {
                return new Main.Number(evalConst(new Main.Add(leftSimpl, rightSimpl)));
            }
            if (isZero(leftSimpl)) {
                return rightSimpl;
            }
            if (isZero(rightSimpl)) {
                return leftSimpl;
            }
            return new Main.Add(leftSimpl, rightSimpl);
        }
    }
}
