package test;


public class JarTest {
    public static int sum(int a, int b) {
        int ans = util.MyMathUtil.sum1(a, b);
        return ans;
    }

    public static void main(java.lang.String[] args) {
        test.JarTest.sum(0, 1);
    }
}