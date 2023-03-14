package util;

import org.junit.Assert;
import org.junit.Test;
import test.SumTest;

public class TestSumTest {
    @Test
    public void testSum1() {
        int a = 1, b = 3;
        int ans = 4;
        Assert.assertEquals(ans, SumTest.testSum(a, b));

    }

    @Test
    public void testSum2() {
        int a = 2, b = 3;
        int ans = 5;
        Assert.assertEquals(ans, SumTest.testSum(a, b));
    }

    @Test
    public void testSum3() {
        int a = 3, b = 3;
        int ans = 6;
        Assert.assertEquals(ans, SumTest.testSum(a, b));
    }

    @Test
    public void testSum4() {
        int a = 10, b = 3;
        int ans = 13;
        Assert.assertEquals(ans, SumTest.testSum(a, b));
    }

    @Test
    public void testSum5() {
        int a = -1, b = 3;
        int ans = 2;
        Assert.assertEquals(ans, SumTest.testSum(a, b));
    }
}
