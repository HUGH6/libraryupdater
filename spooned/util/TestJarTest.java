package util;





public class TestJarTest {
    @org.junit.Test
    public void testJar1() {
        int a = 1;         int b = 2;
        int n = test.JarTest.sum(a, b);
        org.junit.Assert.assertEquals(3, n);

    }

    @org.junit.Test
    public void testJar2() {
        int a = 2;         int b = 2;
        int n = test.JarTest.sum(a, b);
        org.junit.Assert.assertEquals(4, n);
    }

    @org.junit.Test
    public void testJar3() {
        int a = 2;         int b = 4;
        int n = test.JarTest.sum(a, b);
        org.junit.Assert.assertEquals(6, n);
    }
}