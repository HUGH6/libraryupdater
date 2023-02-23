package core.solutionsearch.population;

import core.validation.entity.TestCaseVariantValidationResult;
import core.validation.entity.VariantValidationResult;

/**
 * 基于测试用例执行结果来生成适应度函数，测试用例执行中失败用例越少越好
 * 适应度值越大越接近1.0越差
 */
public class TestCaseFitnessFunction implements FitnessFunction {
    /**
     * 根据验证结果计算适应度
     * @param validationResult
     * @return
     */
    @Override
    public double calculateFitnessValue(VariantValidationResult validationResult) {
        if (validationResult == null) {
            return getWorstMaxFitnessValue();
        }

        TestCaseVariantValidationResult result = (TestCaseVariantValidationResult) validationResult;
        // 适应度计算公式： fitness = failure / total
        return (double) result.getFailureTestCasesCount() / result.getCasesExecutedCount();
    }

    @Override
    public double getWorstMaxFitnessValue() {
        return 1.0;
    }
}
