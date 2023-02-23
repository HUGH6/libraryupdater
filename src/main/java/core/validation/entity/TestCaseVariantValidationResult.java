package core.validation.entity;

/**
 * 表示单元测试验证程序变体的结果，包含更多结果信息
 */
public interface TestCaseVariantValidationResult extends VariantValidationResult {
    /**
     * 回归是否执行
     * @return
     */
    boolean isRegressionExecuted();

    /**
     * 设置回归是否执行
     * @param regressionExecuted
     */
    void setIsRegressionExecuted(boolean regressionExecuted);

    /**
     * 获取执行通过的单测用例数量
     * @return
     */
    int getPassingTestCasesCount();

    /**
     * 获得执行失败的用例数量
     * @return
     */
    int getFailureTestCasesCount();

    /**
     * 获取执行的用例数量
     * @return
     */
    int getCasesExecutedCount();
}
