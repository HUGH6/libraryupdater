package util;

public class StringUtil1 {
    public static String substring(String str) throws IllegalArgumentException {
        if (str == null) {
            throw new IllegalArgumentException();
        }
        return str.substring(0, 2);
    }
}
