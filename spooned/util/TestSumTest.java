package util;





public class TestSumTest {
    @org.junit.Test
    public void testSum1() {
        int a = 1;         int b = 3;
        int ans = 4;
        org.junit.Assert.assertEquals(ans, test.SumTest.testSum(a, b));

    }

    @org.junit.Test
    public void testSum2() {
        int a = 2;         int b = 3;
        int ans = 5;
        org.junit.Assert.assertEquals(ans, test.SumTest.testSum(a, b));
    }

    @org.junit.Test
    public void testSum3() {
        int a = 3;         int b = 3;
        int ans = 6;
        org.junit.Assert.assertEquals(ans, test.SumTest.testSum(a, b));
    }

    @org.junit.Test
    public void testSum4() {
        int a = 10;         int b = 3;
        int ans = 13;
        org.junit.Assert.assertEquals(ans, test.SumTest.testSum(a, b));
    }

    @org.junit.Test
    public void testSum5() {
        int a = -1;         int b = 3;
        int ans = 2;
        org.junit.Assert.assertEquals(ans, test.SumTest.testSum(a, b));
    }
}