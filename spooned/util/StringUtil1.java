package util;

public class StringUtil1 {
    public static java.lang.String substring(java.lang.String str) throws java.lang.IllegalArgumentException {
        if (str == null) {
            throw new java.lang.IllegalArgumentException();
        }
        return str.substring(0, 2);
    }
}