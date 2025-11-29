package org.example;

public class Multiplication {
    static final class Mul extends Main.Binary {
        Mul(Main.Expression l, Main.Expression r) {
            super(l, r);
        }

        @Override
        protected char opChar() {
            return '*';
        }

        @Override
        protected int apply(int a, int b) {
            return a * b;
        }

        @Override
        public Main.Expression derivative(String v) {
            return new Main.Add(
                    new Main.Mul(left.derivative(v), right),
                    new Main.Mul(left, right.derivative(v))
            );
        }

        @Override
        public Main.Expression simplify() {
            Main.Expression leftSimpl = left.simplify();
            Main.Expression rightSimpl = right.simplify();
            if (isZero(leftSimpl) || isZero(rightSimpl)) {
                return new Main.Number(0);
            }
            if (isOne(leftSimpl)) {
                return rightSimpl;
            }
            if (isOne(rightSimpl)) {
                return leftSimpl;
            }
            if (bothConst(leftSimpl, rightSimpl)) {
                return new Main.Number(evalConst(new Main.Mul(leftSimpl, rightSimpl)));
            }
            return new Main.Mul(leftSimpl, rightSimpl);
        }
    }
}
