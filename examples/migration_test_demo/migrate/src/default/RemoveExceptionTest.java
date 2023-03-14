import other.RemoveExceptionUtil;

import java.io.IOException;

public class RemoveExceptionTest {
    public static void main(String[] args) {

    }

    public void test() {
        try {
            RemoveExceptionUtil.removeExceptionTest();
        } catch (IOException e) {

        } catch (InterruptedException e) {

        }
    }
}
