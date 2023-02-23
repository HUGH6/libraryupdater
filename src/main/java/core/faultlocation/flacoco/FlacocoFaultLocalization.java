package core.faultlocation.flacoco;

import conf.ConfigurationProperties;
import core.faultlocation.FaultLocalizationResult;
import core.faultlocation.FaultLocalizationStrategy;
import core.faultlocation.entity.SuspiciousCode;
import core.setup.ProjectMigrationFacade;
import fr.spoonlabs.flacoco.api.Flacoco;
import fr.spoonlabs.flacoco.api.result.FlacocoResult;
import fr.spoonlabs.flacoco.api.result.Location;
import fr.spoonlabs.flacoco.api.result.Suspiciousness;
import fr.spoonlabs.flacoco.core.config.FlacocoConfig;
import fr.spoonlabs.flacoco.core.coverage.framework.JUnit4Strategy;
import fr.spoonlabs.flacoco.core.coverage.framework.JUnit5Strategy;
import fr.spoonlabs.flacoco.core.test.TestContext;
import fr.spoonlabs.flacoco.core.test.TestDetector;
import fr.spoonlabs.flacoco.core.test.method.TestMethod;
import fr.spoonlabs.flacoco.localization.spectrum.SpectrumFormula;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 基于flacoco进行故障定位
 */
public class FlacocoFaultLocalization implements FaultLocalizationStrategy {
    private static final Logger logger = Logger.getLogger(FlacocoFaultLocalization.class.getName());

    List<TestContext> testContexts = new ArrayList<>();
    /**
     * 进行故障定位，搜索可疑的故障语句
     * @param facade
     * @param testToRun
     * @return
     */
    @Override
    public FaultLocalizationResult searchSuspicious(ProjectMigrationFacade facade, List<String> testToRun) throws Exception {
        FlacocoConfig config = setupFlacocoConfig(facade);

        // We force the detection of test cases
//        if (this.testContexts == null || testContexts.isEmpty()) {
//            this.testContexts = new TestDetector(config).getTests();
//        }

        // We put the test cases names in the flacoco configuration
//        setupTestCasesToExecute(config, this.testContexts, testToRun);

        Flacoco flacoco = new Flacoco(config);
        FlacocoResult flacocoResult = flacoco.run();

        List<SuspiciousCode> candidates = new ArrayList<>();
        int i = 0;
        for (Map.Entry<Location, Suspiciousness> entry : flacocoResult.getDefaultSuspiciousnessMap().entrySet()) {
            double suspvalue = entry.getValue().getScore();
            String className = entry.getKey().getClassName();
            Integer lineNumber = entry.getKey().getLineNumber();

            logger.info("Suspicious: " + ++i + " line " + className + " l: " + lineNumber + ", susp " + suspvalue);

            SuspiciousCode sc = new SuspiciousCode(className, null, lineNumber, suspvalue, null);
            candidates.add(sc);
        }

        FaultLocalizationResult result = new FaultLocalizationResult(candidates, flacocoResult.getFailingTests()
                .stream().map(TestMethod::getFullyQualifiedClassName).distinct().collect(Collectors.toList()));

        result.setExecutedTestCasesMethods(flacocoResult.getExecutedTests().stream()
                .map(TestMethod::getFullyQualifiedMethodName).distinct().collect(Collectors.toList()));

        result.setFailingTestCasesMethods(flacocoResult.getFailingTests().stream()
                .map(TestMethod::getFullyQualifiedMethodName).distinct().collect(Collectors.toList()));

        if (facade.getProjectConfiguration().getFailingTestCases().isEmpty()) {
            logger.debug("Failing test cases was not passed as argument: we use the results from running them"
                    + result.getFailingTestCasesClasses());
            facade.getProjectConfiguration().setFailingTestCases(result.getFailingTestCasesClasses());
        }

        return result;
    }

    /**
     * 加载用于故障定位的测试用例
     * @param facade
     * @return
     */
    @Override
    public List<String> findTestCasesToExecute(ProjectMigrationFacade facade) {
        FlacocoConfig config = setupFlacocoConfig(facade);
        this.testContexts = new TestDetector(config).getTests();
        return this.testContexts.stream().flatMap(x -> x.getTestMethods().stream())
                .map(TestMethod::getFullyQualifiedClassName).distinct().collect(Collectors.toList());
    }

    /**
     * 设置flacoco配置
     * @param facade
     * @return
     */
    private FlacocoConfig setupFlacocoConfig(ProjectMigrationFacade facade) {
        FlacocoConfig config = new FlacocoConfig();
//        config.setThreshold(ConfigurationProperties.getPropertyDouble(ConfigurationProperties.FAULT_LOCALIZATION_THRESHOLD));

        // Handle project location configuration
        Integer timeOut = 0;
        if (ConfigurationProperties.getProperty(ConfigurationProperties.MAX_TIME) != null) {
            timeOut = Integer.valueOf(ConfigurationProperties.getProperty(ConfigurationProperties.MAX_TIME));
            if (timeOut == 0) {
                timeOut = 10;
            }
        }

        config.setTestRunnerTimeoutInMs(timeOut * 60000);
//        config.setTestRunnerJVMArgs(null);
        config.setProjectPath(facade.getProjectConfiguration().getOriginalDirProjectRoot());
        config.setClasspath(facade.getProjectConfiguration().getDependenciesString() + File.pathSeparator + System.getProperty("java.class.path"));
        config.setComplianceLevel(8);
        config.setFamily(FlacocoConfig.FaultLocalizationFamily.SPECTRUM_BASED);
        config.setSpectrumFormula(SpectrumFormula.OCHIAI);
//        config.setSrcJavaDir(facade.getProjectConfiguration().getOriginalDirSrc());
//        config.setSrcTestDir(facade.getProjectConfiguration().getOriginalDirTestSrc());
//        if (facade.getProjectConfiguration().getOriginalDirBin() != null) {
//            config.setBinJavaDir(facade.getProjectConfiguration().getOriginalDirBin());
//        }
//        if (facade.getProjectConfiguration().getOriginalDirTestBin() != null) {
//            config.setBinTestDir(facade.getProjectConfiguration().getOriginalDirTestBin());
//        }

        // Handle manually set includes/excludes
//        if (ConfigurationProperties.getProperty(ConfigurationProperties.PACKAGE_TO_INSTRUMENT) != null
//                && !ConfigurationProperties.getProperty(ConfigurationProperties.PACKAGE_TO_INSTRUMENT).isEmpty()) {
//            String option = ConfigurationProperties.getProperty(ConfigurationProperties.PACKAGE_TO_INSTRUMENT);
//            if (!option.endsWith(".*")) {
//                option += ".*";
//            }
//            config.setJacocoIncludes(Collections.singleton(option));
//        }
//        config.setJacocoExcludes(Collections.singleton("java.*"));

        // Handle test configuration
//        config.setjUnit4Tests(new HashSet<>());
//        config.setjUnit5Tests(new HashSet<>());

        return config;
    }

    /**
     * 设置待执行的单元测试
     * @param config
     * @param testContexts
     * @param testToRun
     */
    private void setupTestCasesToExecute(FlacocoConfig config, List<TestContext> testContexts, List<String> testToRun) {
        List<String> testMethodToRun = testToRun.stream().filter(e -> e.contains("#")).collect(Collectors.toList());
        List<String> testClassesToRun = testToRun.stream().filter(e -> !e.contains("#")).collect(Collectors.toList());

        for (TestContext testContext : testContexts) {
            if (testContext.getTestFrameworkStrategy() instanceof JUnit4Strategy) {
                Set<String> tmp = testContext.getTestMethods().stream()
                        .filter((e) -> {
                            if (testToRun.isEmpty() || (testMethodToRun.contains(e.getFullyQualifiedMethodName()) || testClassesToRun.contains(e.getFullyQualifiedClassName()))) {
                                return true;
                            } else {
                                return false;
                            }
                        })
                        .map(TestMethod::getFullyQualifiedMethodName).collect(Collectors.toSet());
                config.setjUnit4Tests(tmp);
            } else if (testContext.getTestFrameworkStrategy() instanceof JUnit5Strategy) {
                config.setjUnit5Tests(testContext.getTestMethods().stream()
                        .filter(e -> testToRun.isEmpty() || (testMethodToRun.contains(e.getFullyQualifiedMethodName())
                                || testClassesToRun.contains(e.getFullyQualifiedClassName())))
                        .map(TestMethod::getFullyQualifiedMethodName).collect(Collectors.toSet()));
            }
        }
    }
}
