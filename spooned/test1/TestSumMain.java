package test1;




public class TestSumMain {
    @org.junit.Test
    public void testSum1() {
        int a = 1;         int b = 2;
        int n = test1.SumMain.sum(a, b);
        org.junit.Assert.assertEquals(3, n);

    }

    @org.junit.Test
    public void testSum2() {
        int a = 2;         int b = 2;
        int n = test1.SumMain.sum(a, b);
        org.junit.Assert.assertEquals(4, n);
    }

    @org.junit.Test
    public void testSum3() {
        int a = 2;         int b = 4;
        int n = test1.SumMain.sum(a, b);
        org.junit.Assert.assertEquals(6, n);
    }
}