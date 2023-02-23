package util;

public class MathUtil2 {
    public static int sum(int a, int b, int c) {
        return (a + b) + c;
    }

    public static int twoTimesFor(int a, boolean twoTimes) {
        if (twoTimes) {
            return a + a;
        }
        return a;
    }
}