package util;

import org.junit.Assert;
import org.junit.Test;
import test.SortTest;


public class TestSortTest {
    @Test
    public void testSort1() {
        int [] arr = {3,2,4,6,5};
        int n = SortTest.getSecondLargeNumber(arr);
        Assert.assertEquals(5, n);

    }

    @Test
    public void testSort2() {
        int [] arr = {3,2,4,6,5,7};
        int n = SortTest.getSecondLargeNumber(arr);
        Assert.assertEquals(6, n);
    }

    @Test
    public void testSort3() {
        int [] arr = {3,2,4,6,5,1};
        int n = SortTest.getSecondLargeNumber(arr);
        Assert.assertEquals(5, n);
    }

    @Test
    public void testSort4() {
        int [] arr = {3,2,6,5};
        int n = SortTest.getSecondLargeNumber(arr);
        Assert.assertEquals(5, n);
    }

    @Test
    public void testSort5() {
        int [] arr = {};
        int n = SortTest.getSecondLargeNumber(arr);
        Assert.assertEquals(-1, n);
    }
}
