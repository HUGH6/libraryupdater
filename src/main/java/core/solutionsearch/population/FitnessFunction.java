package core.solutionsearch.population;

import core.validation.entity.VariantValidationResult;

public interface FitnessFunction {
    /**
     * 根据验证结果计算适应度
     * @param validationResult
     * @return
     */
    double calculateFitnessValue(VariantValidationResult validationResult);

    /**
     * 返回默认的最差适应度
     * @return
     */
    double getWorstMaxFitnessValue();
}
