package other;

import java.io.IOException;

public class RemoveExceptionUtil {
    public static void removeExceptionTest() throws IOException, InterruptedException {
        if (1 == 1) {
            throw new IOException();
        } else {
            throw new InterruptedException();
        }

    }
}
