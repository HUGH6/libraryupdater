package util;

import org.junit.Assert;
import org.junit.Test;
import test.TwoTimesTest;

import java.net.MalformedURLException;

public class TestTwoTimesTest {
    @Test
    public void testTowTime1() throws MalformedURLException {
        int a = 1;
        int ans = 2;
        Assert.assertEquals(ans, TwoTimesTest.testTwoTimes(a));

    }

    @Test
    public void testTowTime2() throws MalformedURLException {
        int a = 2;
        int ans = 4;
        Assert.assertEquals(ans, TwoTimesTest.testTwoTimes(a));
    }

    @Test
    public void testTowTime3() throws MalformedURLException {
        int a = 3;
        int ans = 6;
        Assert.assertEquals(ans, TwoTimesTest.testTwoTimes(a));
    }

    @Test
    public void testTowTime4() throws MalformedURLException {
        int a = 4;
        int ans = 8;
        Assert.assertEquals(ans, TwoTimesTest.testTwoTimes(a));
    }
}
