package test;


public class JarMain {
    public static int sum(int a , int b) {
        int ans = util.MyMathUtil.sum1(a, b);
        return ans;
    }

    public static void main(String[] args) {
        sum(0, 1);
    }
}
