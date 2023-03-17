package test1;


public class SumMain {
    public static int sum(int a , int b) {
        System.out.println(a);
        System.out.println(b);

        int ans = util.MathUtil.sum(a, b);

        return ans;
    }

    public static void main(String[] args) {
        System.out.println(sum(1, 5));
    }
}
