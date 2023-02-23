package core.faultlocation.entity.runtestsuite;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 * 用于检索类路径中的 JUnit4 测试类的 ClassTester 实现。
 * 您可以指定是否要在搜索中包含 jar 文件，并且可以提供一组正则表达式来指定要包含的类名。
 */
public class TestFilter implements ClassFilter {
    // 是否在jar中搜索
    private boolean searchInJars;
    // 测试类型
    private TestType[] testTypes;

    public TestFilter() {
        this.searchInJars = true;
        this.testTypes = new TestType[] {
            TestType.JUNIT38_TEST_CLASSES,
            TestType.RUN_WITH_CLASSES,
            TestType.TEST_CLASSES
        };
    }

    public TestFilter(boolean searchInJars) {
        this.searchInJars = searchInJars;
        this.testTypes = new TestType[] {
                TestType.JUNIT38_TEST_CLASSES,
                TestType.RUN_WITH_CLASSES,
                TestType.TEST_CLASSES
        };
    }

    public TestFilter(TestType[] suiteTypes) {
        this.searchInJars = true;
        this.testTypes = suiteTypes;
    }

    public TestFilter(boolean searchInJars, TestType[] suiteTypes) {
        this.searchInJars = searchInJars;
        this.testTypes = suiteTypes;
    }

    @Override
    public boolean acceptClass(Class<?> clazz) {
        // We directly ignore abstract class
        if (isAbstractClass(clazz)) {
            return false;
        }

        if (isInSuiteTypes(TestType.TEST_CLASSES)) {
            if (acceptTestClass(clazz)) {
                return true;
            }
            if (acceptTestClassJUnit5(clazz)) {
                return true;
            }
        }
        if (isInSuiteTypes(TestType.JUNIT38_TEST_CLASSES)) {
            if (acceptJUnit38Test(clazz)) {
                return true;
            }
        }
        if (isInSuiteTypes(TestType.RUN_WITH_CLASSES)) {
            return acceptRunWithClass(clazz);
        }

        return false;
    }

    @Override
    public boolean acceptClassName(String className) {
        return true;
    }

    @Override
    public boolean acceptInnerClass() {
        return true;
    }

    @Override
    public boolean searchInJars() {
        return searchInJars;
    }

    private boolean acceptJUnit38Test(Class<?> clazz) {
        if (isAbstractClass(clazz)) {
            return false;
        }
        return TestCase.class.isAssignableFrom(clazz);
    }

    private boolean acceptRunWithClass(Class<?> clazz) {
        return clazz.isAnnotationPresent(RunWith.class);
    }

    private boolean isInSuiteTypes(TestType testType) {
        return Arrays.asList(testTypes).contains(testType);
    }

    private boolean isAbstractClass(Class<?> clazz) {
        return (clazz.getModifiers() & Modifier.ABSTRACT) != 0;
    }

    private boolean acceptTestClass(Class<?> clazz) {
        if (isAbstractClass(clazz)) {
            return false;
        }
        try {
            for (Method method : clazz.getMethods()) {
                if (method.getAnnotation(Test.class) != null) {
                    return true;
                }
            }
        } catch (NoClassDefFoundError ignore) {
        } catch (java.lang.VerifyError e) {
            e.printStackTrace();
        } catch (java.lang.ClassFormatError e) {
            e.printStackTrace();
        }

		return false;
    }

    private boolean acceptTestClassJUnit5(Class<?> clazz) {
        if (isAbstractClass(clazz)) {
            return false;
        }

        try {
            for (Method method : clazz.getMethods()) {
                if (method.getAnnotation(org.junit.jupiter.api.Test.class) != null) {
                    return true;
                }
            }
        } catch (NoClassDefFoundError ignore) {
        } catch (VerifyError e) {
            e.printStackTrace();
        } catch (ClassFormatError e) {
            e.printStackTrace();
        }

        return false;
    }
}
