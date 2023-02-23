package util;





public class TestSubstringTest {
    @org.junit.Test
    public void testSubstring1() {
        java.lang.String str = "hello world";
        java.lang.String ans = "he";
        org.junit.Assert.assertEquals(ans, test.SubstringTest.testSubstring(str));

    }

    @org.junit.Test
    public void testSubstring2() {
        java.lang.String str = "world";
        java.lang.String ans = "wo";
        org.junit.Assert.assertEquals(ans, test.SubstringTest.testSubstring(str));
    }

    @org.junit.Test
    public void testSubstring3() {
        java.lang.String str = "good";
        java.lang.String ans = "go";
        org.junit.Assert.assertEquals(ans, test.SubstringTest.testSubstring(str));
    }

    @org.junit.Test
    public void testSubstring4() {
        java.lang.String str = "name";
        java.lang.String ans = "na";
        org.junit.Assert.assertEquals(ans, test.SubstringTest.testSubstring(str));
    }

    @org.junit.Test
    public void testSubstring5() {
        java.lang.String str = "test";
        java.lang.String ans = "te";
        org.junit.Assert.assertEquals(ans, test.SubstringTest.testSubstring(str));
    }
}