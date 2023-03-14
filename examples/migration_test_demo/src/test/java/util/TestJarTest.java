package util;

import org.junit.Assert;
import org.junit.Test;
import test.JarTest;

public class TestJarTest {
    @Test
    public void testJar1() {
        int a = 1, b = 2;
        int n = JarTest.sum(a, b);
        Assert.assertEquals(3, n);

    }

    @Test
    public void testJar2() {
        int a = 2, b = 2;
        int n = JarTest.sum(a, b);
        Assert.assertEquals(4, n);
    }

    @Test
    public void testJar3() {
        int a = 2, b = 4;
        int n = JarTest.sum(a, b);
        Assert.assertEquals(6, n);
    }
}
