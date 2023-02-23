package core.setup;

import core.entity.ProgramVariant;
import core.faultlocation.entity.TestClassesFinder;
import org.apache.log4j.Logger;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * 用于访问单元测试代码的门面类
 */
public class TestCasesFinderFacade {

    protected static Logger logger = Logger.getLogger(TestCasesFinderFacade.class.getName());

    public static List<String> findJUnit4XTestCasesForRegression(ProjectMigrationFacade projectFacade) {
        String classPath = projectFacade.getMutatorOutDirWithPrefix(ProgramVariant.DEFAULT_ORIGINAL_VARIANT);
        String cp = projectFacade.getProjectConfiguration().getDependenciesString();
        classPath += File.pathSeparator + cp;
        String[] testClassesRegression = new TestClassesFinder().findIn(classpathFrom(classPath), false);
        List<String> testCasesRegression = Arrays.asList(testClassesRegression);
        return testCasesRegression;
    }

    /**
     * 将classpath字符串中转换为URL数组
     * @param classpath
     * @return
     */
    public static URL[] classpathFrom(String classpath) {
        String[] folderNames = classpath.split(File.pathSeparator);
        URL[] folders = new URL[folderNames.length];
        int index = 0;
        for (String folderName : folderNames) {
            folders[index++] = stringToUrl(folderName);
        }

        return folders;
    }

    /**
     * 将字符串路径转换为URL
     * @param path
     * @return
     */
    public static URL stringToUrl(String path) {
        URL url = null;
        try {
            url = new File(path).toURI().toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }
}
