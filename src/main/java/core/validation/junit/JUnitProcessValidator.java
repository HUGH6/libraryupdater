package core.validation.junit;

import conf.ConfigurationProperties;
import core.entity.ProgramVariant;
import core.migration.util.MigrationSupporter;
import core.setup.ProjectMigrationFacade;
import core.setup.TestCasesFinderFacade;
import core.validation.ProgramVariantValidator;
import core.validation.entity.TestCaseVariantValidationResult;
import core.validation.entity.TestCaseProgramValidationResultImpl;
import core.validation.entity.TestResult;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import util.Converters;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JUnitProcessValidator implements ProgramVariantValidator  {
    private static Logger logger = Logger.getLogger(Thread.currentThread().getName());

    /**
     * 通过回归测试执行单元测试验证一个程序变体
     * @param variant
     * @param projectFacade
     * @return
     */
    @Override
    public TestCaseVariantValidationResult validate(ProgramVariant variant, ProjectMigrationFacade projectFacade) {
        try {
            URL[] classPath = createClassPath(variant, projectFacade);
            // 执行单测回归
            TestCaseVariantValidationResult result = runRegression(variant, projectFacade, classPath);
            // 执行单元测试之后，需要移除程序变体的字节码
            removeOfCompiledCode(variant, projectFacade);
            return result;
        } catch (MalformedURLException e) {
            logger.error("run regression error, variant " + variant.getId());

            removeOfCompiledCode(variant, projectFacade);
            return null;
        }
    }

    /**
     * 运行回归测试
     * @param mutatedVariant
     * @param projectFacade
     * @param classPath
     * @return
     */
    protected TestCaseVariantValidationResult runRegression(ProgramVariant mutatedVariant,
                                                            ProjectMigrationFacade projectFacade, URL[] classPath) {
        JUnitProcessLaucher testProcessRunner = new JUnitProcessLaucher();
        return executeRegressionTesting(mutatedVariant, classPath, testProcessRunner, projectFacade);
    }

    /**
     * 执行程序变体的回归测试
     * @param mutatedVariant
     * @param classPath
     * @param p
     * @param projectFacade
     * @return
     */
    protected TestCaseVariantValidationResult executeRegressionTesting(ProgramVariant mutatedVariant, URL[] classPath,
                                                                       JUnitProcessLaucher p, ProjectMigrationFacade projectFacade) {
        logger.debug("executing regression");

        // 需要执行的单测
        List<String> testCasesRegression = projectFacade.getProjectConfiguration().getRegressionTestCases();
        // jvm路径
        String jvmPath = ConfigurationProperties.getProperty(ConfigurationProperties.JVM_FOR_TEST_EXECUTION);
        // 执行单测
        TestResult regressionResult = p.execute(jvmPath, classPath, testCasesRegression,
                ConfigurationProperties.getPropertyInt(ConfigurationProperties.MAX_TEST_SUITE_TIME));

        if (testCasesRegression == null || testCasesRegression.isEmpty()) {
            logger.error("any test case for regression testing");
            return null;
        }

        if (regressionResult == null) {
            return null;
        } else {
            logger.debug(regressionResult);

            return new TestCaseProgramValidationResultImpl(regressionResult, regressionResult.isSuccessful(),
                    (regressionResult != null));
        }
    }

    /**
     * 查找用于验证的测试用例
     * @param projectFacade
     * @return
     */
    @Override
    public List<String> findTestCasesToExecute(ProjectMigrationFacade projectFacade) {
        return TestCasesFinderFacade.findJUnit4XTestCasesForRegression(projectFacade);
    }

    /**
     * 获得测试项目的classpath，并添加上gzoltar依赖
     * @param mutatedVariant
     * @param projectFacade
     * @return
     * @throws MalformedURLException
     */
    protected URL[] createClassPath(ProgramVariant mutatedVariant, ProjectMigrationFacade projectFacade)
            throws MalformedURLException {
        List<URL> originalURL = createOriginalURLs(projectFacade);
        URL[] classpath;
        if (mutatedVariant.getCompilation() != null) {
            // 保存编译的字节码到磁盘，返回对应文件夹的File对象
            File variantOutputFile = defineLocationOfCompiledCode(mutatedVariant, projectFacade);
            // 将编译的字节码的URL也加入URL数组
            classpath = Converters.redefineURL(variantOutputFile, originalURL.toArray(new URL[0]));
        } else {
            classpath = originalURL.toArray(new URL[0]);
        }

//        // 判断是否有gzoltar
//        boolean isGZoltarDependencyFound = false;
//        for (int i = 0; i < classpath.length && !isGZoltarDependencyFound; i++) {
//            if (classpath[i].getFile().contains("gzoltar-0.1.1")) {
//                isGZoltarDependencyFound = true;
//            }
//        }
//
//        // 如果classpath中没有gzoltar，则从本项目的lib目录下添加gzoltar的jar依赖
//        if (!isGZoltarDependencyFound) {
//            File libsfolder = new File("." + File.separator + "lib");
//
//            URL[] newBc = new URL[classpath.length + 1];
//            newBc[0] = new URL("file://" + libsfolder.getAbsolutePath() + File.separator
//                    + "com.gzoltar-0.1.1-jar-with-dependencies.jar");
//            for (int i = 0; i < classpath.length; i++) {
//                newBc[i + 1] = classpath[i];
//            }
//
//            return newBc;
//        }

        return classpath;
    }

    /**
     * 获得测试项目原始的classpath列表
     * @param projectFacade
     * @return
     * @throws MalformedURLException
     */
    public List<URL> createOriginalURLs(ProjectMigrationFacade projectFacade) throws MalformedURLException {
        URL[] defaultSUTClasspath = projectFacade.getClassPathURLForProgramVariant(ProgramVariant.DEFAULT_ORIGINAL_VARIANT);
        List<URL> originalURL = new ArrayList<>(Arrays.asList(defaultSUTClasspath));

        String classpath = System.getProperty("java.class.path");

        for (String path : classpath.split(File.pathSeparator)) {
            if (path.contains("IDEA")) {
                continue;
            }
            File f = new File(path);
            originalURL.add(f.toURL());
        }

        return originalURL;
    }

    /**
     * 保存编译的字节码到磁盘，返回对于目录的File对象
     * @param mutatedVariant
     * @param projectFacade
     * @return
     */
    protected File defineLocationOfCompiledCode(ProgramVariant mutatedVariant, ProjectMigrationFacade projectFacade) {
        String bytecodeOutput = projectFacade.getMutatorOutDirWithPrefix(mutatedVariant.currentMutatorIdentifier());
        File variantOutputFile = new File(bytecodeOutput);
        MigrationSupporter.getSupporter().getOutputer().saveByteCode(mutatedVariant.getCompilation(), variantOutputFile);
        return variantOutputFile;
    }

    /**
     * 删除程序变体的字节码
     * @param mutatedVariant
     * @param projectFacade
     */
    protected void removeOfCompiledCode(ProgramVariant mutatedVariant, ProjectMigrationFacade projectFacade) {
        String bytecodeOutput = projectFacade.getMutatorOutDirWithPrefix(mutatedVariant.currentMutatorIdentifier());
        File variantOutputFile = new File(bytecodeOutput);

        try {
            FileUtils.deleteDirectory(variantOutputFile);
        } catch (IOException e) {
            logger.error("Cannot removed variant bin: " + e.getMessage());
        }
    }
}
