package other;



public class RemoveExceptionUtil {
    public static void removeExceptionTest() throws java.io.IOException, java.lang.InterruptedException {
        if (1 == 1) {
            throw new java.io.IOException();
        } else {
            throw new java.lang.InterruptedException();
        }

    }
}