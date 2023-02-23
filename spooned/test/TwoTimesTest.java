package test;



public class TwoTimesTest {
    public static void main(java.lang.String[] args) {

    }

    public static int testTwoTimes(int num) {
        java.lang.System.out.println(num);

        int age = num;

        java.lang.System.out.println(age);

        int res = util.MathUtil1.twoTimes(age);
        return res;
    }

}