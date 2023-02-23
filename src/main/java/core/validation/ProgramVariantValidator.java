package core.validation;

import core.entity.ProgramVariant;
import core.setup.ProjectMigrationFacade;
import core.validation.entity.VariantValidationResult;

import java.util.List;

/**
 * 程序变体验证器
 */
public interface ProgramVariantValidator {
    /**
     * 验证一个程序变体
     * @param variant
     * @param projectFacade
     * @return
     */
    VariantValidationResult validate(ProgramVariant variant, ProjectMigrationFacade projectFacade);

    /**
     * 查找用于验证的测试用例
     * @param projectFacade
     * @return
     */
    List<String> findTestCasesToExecute(ProjectMigrationFacade projectFacade);
}
