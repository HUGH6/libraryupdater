package core.validation;

import core.validation.entity.ValidatorTypeEnum;
import core.validation.junit.JUnitProcessValidator;

/**
 * validator工场
 */
public class ValidatorFactory {
    public static ProgramVariantValidator createValidator(ValidatorTypeEnum type) {
        switch (type) {
            case PROCESS:
                return new JUnitProcessValidator();
            default:
                // 默认返回基于进程的验证器
                return new JUnitProcessValidator();
        }
    }
}
