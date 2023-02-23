package core.validation.junit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Result;

import java.util.List;

public class TestJUnitSuiteExternalExecutor {
    JUnitSuiteExternalExecutor executor = null;

    @Before
    public void init() {
        this.executor = new JUnitSuiteExternalExecutor();
    }

    @Test
    public void testGetClassesToRun() {
        String [] classesToRun = {
                "core.ingredient.util.TestStringDistance"
        };
        try {
            List<Class<?>> classList = this.executor.getClassesToRun(classesToRun);
            System.out.println(classList);
            Assert.assertEquals(classesToRun.length, classList.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRun() {
        String [] classesToRun = {
                "core.ingredient.util.TestStringDistance"
        };
        try {
            Result res = this.executor.run(classesToRun);
            Assert.assertNotNull(res);
            Assert.assertEquals(0, res.getFailureCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFailureOutput() {
        String [] classesToRun = {
                "core.ingredient.util.TestStringDistance"
        };
        try {
            Result res = this.executor.run(classesToRun);
            Assert.assertNotNull(res);
            Assert.assertEquals(0, res.getFailureCount());

            String output = this.executor.getFailureOutput(res);
            Assert.assertNotNull(output);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
