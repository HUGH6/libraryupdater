package test;

import util.MathUtil1;

public class SumTest {
    public static void main(String[] args) {

    }

    public static int testSum(int a, int b) {
        System.out.println(a);
        System.out.println(b);
        int c = MathUtil1.sum(a, b);
        return c;
    }
}
