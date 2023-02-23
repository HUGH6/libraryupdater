package util;







public class TestTwoTimesTest {
    @org.junit.Test
    public void testTowTime1() throws java.net.MalformedURLException {
        int a = 1;
        int ans = 2;
        org.junit.Assert.assertEquals(ans, test.TwoTimesTest.testTwoTimes(a));

    }

    @org.junit.Test
    public void testTowTime2() throws java.net.MalformedURLException {
        int a = 2;
        int ans = 4;
        org.junit.Assert.assertEquals(ans, test.TwoTimesTest.testTwoTimes(a));
    }

    @org.junit.Test
    public void testTowTime3() throws java.net.MalformedURLException {
        int a = 3;
        int ans = 6;
        org.junit.Assert.assertEquals(ans, test.TwoTimesTest.testTwoTimes(a));
    }

    @org.junit.Test
    public void testTowTime4() throws java.net.MalformedURLException {
        int a = 4;
        int ans = 8;
        org.junit.Assert.assertEquals(ans, test.TwoTimesTest.testTwoTimes(a));
    }
}