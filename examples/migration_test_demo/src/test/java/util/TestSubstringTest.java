package util;

import org.junit.Assert;
import org.junit.Test;
import test.SubstringTest;

public class TestSubstringTest {
    @Test
    public void testSubstring1() {
        String str = "hello world";
        String ans = "he";
        Assert.assertEquals(ans, SubstringTest.testSubstring(str));

    }

    @Test
    public void testSubstring2() {
        String str = "world";
        String ans = "wo";
        Assert.assertEquals(ans, SubstringTest.testSubstring(str));
    }

    @Test
    public void testSubstring3() {
        String str = "good";
        String ans = "go";
        Assert.assertEquals(ans, SubstringTest.testSubstring(str));
    }

    @Test
    public void testSubstring4() {
        String str = "name";
        String ans = "na";
        Assert.assertEquals(ans, SubstringTest.testSubstring(str));
    }

    @Test
    public void testSubstring5() {
        String str = "test";
        String ans = "te";
        Assert.assertEquals(ans, SubstringTest.testSubstring(str));
    }
}
