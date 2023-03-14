package test;

import util.StringUtil1;

public class SubstringTest {
    public static void main(String[] args) {

    }

    public static String testSubstring(String str) {
        String res = null;
        try {
            res = StringUtil1.substring(str);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return res;
    }
}
