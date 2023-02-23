package core.faultlocation.entity;

import core.faultlocation.entity.runtestsuite.ClassloaderFinder;
import core.faultlocation.entity.runtestsuite.TestFilter;
import core.faultlocation.entity.runtestsuite.TestSuiteProcessor;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestClassesFinder implements Callable<Collection<Class<?>>> {

    /**
     * 加载测试类
     * @return
     * @throws Exception
     */
    @Override
    public Collection<Class<?>> call() throws Exception {
        Class<?>[] classes = new TestSuiteProcessor(
            new ClassloaderFinder((URLClassLoader) Thread.currentThread().getContextClassLoader()),
            new TestFilter()
        ).process();

        return Arrays.asList(classes);
    }

    public String[] findIn(ClassLoader dumpedToClassLoader, boolean acceptTestSuite) {
        ExecutorService executor = Executors.newSingleThreadExecutor(new CustomClassLoaderThreadFactory(dumpedToClassLoader));

        String[] testClasses;
        try {
            Collection<Class<?>> classes = executor.submit(new TestClassesFinder()).get();
            testClasses = namesFrom(classes);
        } catch (InterruptedException ie) {
            throw new RuntimeException(ie);
        } catch (ExecutionException ee) {
            ee.printStackTrace();
            return null;
        } finally {
            executor.shutdown();
        }

        if (!acceptTestSuite) {
            testClasses = removeTestSuite(testClasses);
        }

        return testClasses;
    }

    public String[] findIn(final URL[] classpath, boolean acceptTestSuite) {
        return findIn(new URLClassLoader(classpath, Thread.currentThread().getContextClassLoader()), acceptTestSuite);
    }

    /**
     * 获取类名称
     * @param classes
     * @return
     */
    protected String[] namesFrom(Collection<Class<?>> classes) {
        String[] names = new String[classes.size()];

        int idx = 0;
        for (Class<?> clazz : classes) {
            names[idx++] = clazz.getName();
        }

        return names;
    }

    /**
     * 移除以Suite结尾的类
     * @param totalTest
     * @return
     */
    public String[] removeTestSuite(String[] totalTest) {
        List<String> tests = new ArrayList<>();
        for (int i = 0; i < totalTest.length; i++) {
            if (!totalTest[i].endsWith("Suite")) {
                tests.add(totalTest[i]);
            }
        }

        return tests.toArray(new String[0]);
    }
}
