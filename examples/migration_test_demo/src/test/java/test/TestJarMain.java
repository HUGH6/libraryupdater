package test;

import org.junit.Assert;
import org.junit.Test;

public class TestJarMain {
    @Test
    public void testSum1() {
        int a = 1, b = 2;
        int n = JarMain.sum(a, b);
        Assert.assertEquals(3, n);

    }

    @Test
    public void testSum2() {
        int a = 2, b = 2;
        int n = JarMain.sum(a, b);
        Assert.assertEquals(4, n);
    }

    @Test
    public void testSum3() {
        int a = 2, b = 4;
        int n = JarMain.sum(a, b);
        Assert.assertEquals(6, n);
    }
}
