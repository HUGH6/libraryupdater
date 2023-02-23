package core.validation.entity;

/**
 * 记录程序变体运行单元测试用例的结果
 */
public class TestCaseProgramValidationResultImpl implements TestCaseVariantValidationResult {
    // 失败的用例数
    int failingTestCasesNumber = 0;
    // 成功的用例数
    int passingTestCasesNumber = 0;
    // 回归测试是否执行
    boolean isRegressionExecuted = false;
    // 结果是否成功
    boolean isValidateSuccess = false;
    /**
     * Indicates whether where were a problem during the execution that stop
     * finishing the complete execution , example Infinite loop
     **/
    boolean executionError = false;
    // 测试执行结果
    TestResult testResult;

    public TestCaseProgramValidationResultImpl(TestResult result) {
        super();
        setTestResult(result);
    }

    public TestCaseProgramValidationResultImpl(boolean errorExecution) {
        this.executionError = errorExecution;
        this.testResult = null;
        this.isRegressionExecuted = false;
        this.isValidateSuccess = false;
        this.failingTestCasesNumber = 0;
        this.passingTestCasesNumber = 0;
    }

    public TestCaseProgramValidationResultImpl(TestResult result, boolean isValidateSuccess, boolean isRegressionExecuted) {
        this(result);
        this.isRegressionExecuted = isRegressionExecuted;
        this.isValidateSuccess = isValidateSuccess;
    }

    @Override
    public boolean isSuccessful() {
        return failingTestCasesNumber == 0 && this.isValidateSuccess;
    }

    @Override
    public boolean isRegressionExecuted() {
        return this.isRegressionExecuted;
    }

    /**
     * 设置回归是否执行
     * @param regressionExecuted
     */
    @Override
    public void setIsRegressionExecuted(boolean regressionExecuted) {
        this.isRegressionExecuted = regressionExecuted;
    }

    /**
     * 获取执行通过的单测用例数量
     * @return
     */
    @Override
    public int getPassingTestCasesCount() {
        return this.passingTestCasesNumber;
    }

    /**
     * 获得执行失败的用例数量
     * @return
     */
    @Override
    public int getFailureTestCasesCount() {
        return this.failingTestCasesNumber;
    }

    /**
     * 获取执行的用例数量
     * @return
     */
    @Override
    public int getCasesExecutedCount() {
        return getPassingTestCasesCount() + getFailureTestCasesCount();
    }

    public String toString() {
        return printTestResult(this.getTestResult());
    }

    public TestResult getTestResult() {
        return testResult;
    }

    public void setTestResult(TestResult result) {
        this.testResult = result;
        if (result != null) {
            this.passingTestCasesNumber = result.casesExecuted - result.failures;
            this.failingTestCasesNumber = result.failures;
            this.isValidateSuccess = (result.casesExecuted == result.failures);
        }
    }

    protected String printTestResult(TestResult result) {
        if (this.executionError || (result == null)) {
            return "|" + false + "|" + 0 + "|" + 0 + "|" + "[]" + "|";
        }
        return "|" + result.isSuccessful() + "|" + result.failures + "|" + result.casesExecuted + "|" + result.failTest
                + "|";
    }

    public boolean isExecutionError() {
        return executionError;
    }
}
