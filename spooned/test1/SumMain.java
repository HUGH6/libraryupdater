package test1;


public class SumMain {
    public static int sum(int a, int b) {
        java.lang.System.out.println(a);
        java.lang.System.out.println(b);

        int ans = util.MathUtil.sum(a, b);

        return ans;
    }

    public static void main(java.lang.String[] args) {
        java.lang.System.out.println(test1.SumMain.sum(1, 5));
    }
}