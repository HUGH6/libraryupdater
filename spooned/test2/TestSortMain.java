package test2;




public class TestSortMain {
    @org.junit.Test
    public void testSort1() {
        int[] arr = new int[]{ 3, 2, 4, 6, 5 };
        int n = test2.SortMain.getSecondLargeNumber(arr);
        org.junit.Assert.assertEquals(5, n);

    }

    @org.junit.Test
    public void testSort2() {
        int[] arr = new int[]{ 3, 2, 4, 6, 5, 7 };
        int n = test2.SortMain.getSecondLargeNumber(arr);
        org.junit.Assert.assertEquals(6, n);
    }

    @org.junit.Test
    public void testSort3() {
        int[] arr = new int[]{ 3, 2, 4, 6, 5, 1 };
        int n = test2.SortMain.getSecondLargeNumber(arr);
        org.junit.Assert.assertEquals(5, n);
    }

    @org.junit.Test
    public void testSort4() {
        int[] arr = new int[]{ 3, 2, 6, 5 };
        int n = test2.SortMain.getSecondLargeNumber(arr);
        org.junit.Assert.assertEquals(5, n);
    }

    @org.junit.Test
    public void testSort5() {
        int[] arr = new int[]{  };
        int n = test2.SortMain.getSecondLargeNumber(arr);
        org.junit.Assert.assertEquals(-1, n);
    }
}