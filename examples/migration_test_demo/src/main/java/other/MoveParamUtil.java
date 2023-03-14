package other;

public class MoveParamUtil {
    public static void moveParamTest(int a, String b, int c) {
        System.out.println(a);
    }

    public static void moveParamTest(int a, int c, String b) {
        System.out.println(a);
    }
}
