package core;

import conf.ConfigurationProperties;
import core.entity.CompositeExecuteResult;
import core.entity.ExecutionResult;
import core.faultlocation.FaultLocalizationFactory;
import core.faultlocation.FaultLocalizationResult;
import core.faultlocation.FaultLocalizationStrategy;
import core.faultlocation.entity.SuspiciousCode;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class FaultLocalizationMain extends AbstractMain {

    public enum FaultLocalizationTypeEnum {
        FLACOCO;
    }

    /**
     * 对需要迁移的代码点执行迁移，需要在子类中实现实际迁移方法
     * @return 返回迁移结果
     * @throws Exception 可能抛出的异常
     */
    @Override
    protected ExecutionResult run() throws Exception {
        initProject();

        String faultLocalizationMode = ConfigurationProperties.getProperty(ConfigurationProperties.FAULT_LOCALIZATION_TYPE).toUpperCase();
        FaultLocalizationTypeEnum faultLocalizationType = FaultLocalizationTypeEnum.valueOf(faultLocalizationMode);
        FaultLocalizationStrategy faultLocalization = FaultLocalizationFactory.getFaultLocalization(faultLocalizationType);

        String testPath = ConfigurationProperties.getProperty(ConfigurationProperties.ORIGINAL_DIR_TEST);
//        List<String> testsToRun = Arrays.asList(testPath.split(File.pathSeparator));
        List<String> testsToRun = new ArrayList<>();

        CompositeExecuteResult results = new CompositeExecuteResult();

        FaultLocalizationResult result = null;

//        if (testsToRun.size() <= 0) {
//            logger.warn("no test to run");
//            return null;
//        }

        result = faultLocalization.searchSuspicious(this.migrationFacade, testsToRun);

        saveResult(result, faultLocalizationType);
        results.addResult((ExecutionResult) result);

        return (ExecutionResult) results;
    }

    public void saveResult(FaultLocalizationResult result, FaultLocalizationTypeEnum type) {
        String outputDir = projectConfiguration.getWorkingDirRoot();
        File file = new File(outputDir);
        if (!file.exists()) {
            file.exists();
        }

        saveSuspicious(result, type, outputDir);
        saveFailing(result, type, outputDir);
        saveExecuted(result, type, outputDir);
    }

    /**
     * 保存可以可以代码行到文件中
     * @param result
     * @param type
     * @param outputDir
     */
    private void saveSuspicious(FaultLocalizationResult result, FaultLocalizationTypeEnum type, String outputDir) {
        List<SuspiciousCode> suspiciousCodes = result.getCandidates();
        try {
            String fileName = outputDir + File.separator + type + "_suspicious" + ".csv";
            FileWriter fw = new FileWriter(fileName);
            for (SuspiciousCode suspiciousCode : suspiciousCodes) {
                fw.append(suspiciousCode.getClassName() + "," + suspiciousCode.getLineNumber() + ","
                        + suspiciousCode.getSuspiciousValueString());
                fw.append("\n");
            }
            fw.flush();
            fw.close();

            logger.debug("save suspicious in" + fileName);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 保存执行的测试代码
     * @param result
     * @param type
     * @param outputDir
     */
    private void saveExecuted(FaultLocalizationResult result, FaultLocalizationTypeEnum type, String outputDir) {
        List<String> tests = result.getExecutedTestCasesMethods();
        String key = "_executed_tests_";
        saveListTests(type, outputDir, tests, key);
    }

    /**
     * 保存执行失败的测试用例
     * @param result
     * @param type
     * @param outputDir
     */
    private void saveFailing(FaultLocalizationResult result, FaultLocalizationTypeEnum type, String outputDir) {
        List<String> tests = result.getFailingTestCasesMethods();
        String key = "_failing_tests_";
        saveListTests(type, outputDir, tests, key);
    }

    /**
     * 保存单元测试用例信息到文件
     * @param type
     * @param outputDir
     * @param tests
     * @param key
     */
    private void saveListTests(FaultLocalizationTypeEnum type, String outputDir, List<String> tests, String key) {
        try {
            String fileName = outputDir + File.separator + type + key + ".csv";
            FileWriter fw = new FileWriter(fileName);
            for (String aTest : tests) {
                fw.append(aTest);
                fw.append("\n");
            }
            fw.flush();
            fw.close();

            logger.debug("saving results at " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        FaultLocalizationMain main = new FaultLocalizationMain();
        try {
//            main.initProject();
            ExecutionResult result = main.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
