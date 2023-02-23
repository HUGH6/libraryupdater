package util;

import org.junit.Assert;
import org.junit.Test;

public class TestMavenUtil {
    @Test
    public void testFindExecutableOnPath() {
        String mvnPath = MavenUtil.findExecutableOnPath("mvn");
        Assert.assertNotNull(mvnPath);
    }

    @Test
    public void testGetMvnCommand() {
        String mvnPath1 = MavenUtil.getMvnCommand();
        String mvnPath2 = MavenUtil.findExecutableOnPath("mvn");
        Assert.assertEquals(mvnPath1, mvnPath2);
    }
}
