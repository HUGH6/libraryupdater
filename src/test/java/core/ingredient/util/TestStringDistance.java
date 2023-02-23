package core.ingredient.util;

import org.junit.Assert;
import org.junit.Test;

public class TestStringDistance {
    @Test
    public void testCalculateDistance() {
        String str1 = "abc";
        String str2 = "acd";
        Assert.assertEquals(2, StringDistance.calculateDistance(str1, str2));
    }

    @Test
    public void testCalculateDistance2() {
        String str3 = "ac";
        String str4 = "ade";
        Assert.assertEquals(2, StringDistance.calculateDistance(str3, str4));
    }

    @Test
    public void testCalculateDistance3() {
        String str5 = "";
        String str6 = "ade";
        Assert.assertEquals(3, StringDistance.calculateDistance(str5, str6));
    }
}
