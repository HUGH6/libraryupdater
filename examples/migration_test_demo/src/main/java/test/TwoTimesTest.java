package test;

import util.MathUtil1;

public class TwoTimesTest {
    public static void main(String[] args) {

    }

    public static int testTwoTimes(int num) {
        System.out.println(num);

        int age = num;

        System.out.println(age);

        int res = MathUtil1.twoTimes(age);
        return res;
    }

}
