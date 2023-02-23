package test;



public class SubstringTest {
    public static void main(java.lang.String[] args) {

    }

    public static java.lang.String testSubstring(java.lang.String str) {
        java.lang.String res = null;
        try {
            res = util.StringUtil1.substring(str);
        } catch (java.lang.IllegalArgumentException e) {
            e.printStackTrace();
        }
        return res;
    }
}