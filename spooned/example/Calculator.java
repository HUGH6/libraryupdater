package example;

public class Calculator {

    public Calculator() {
    }

    public int calculate(java.lang.String op, int op1, int op2) {

        if (op.equals("+")) {
            return op1 + op2;
        } else if (op.equals("-")) {
            return op1 - op2;
        } else if (op.equals("*")) {
            return op1 / op2;// buggy
        } else if (op.equals("/")) {
            return op1 / op2;
        } else if (op.equals("%")) {
            return op1 % op2;
        }
        throw new java.lang.UnsupportedOperationException(op);
    }
}