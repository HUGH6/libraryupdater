package core.solutionsearch.population;

import core.entity.ProgramVariant;

import java.util.List;

/**
 * 用于选择从程序变体中选择组成下一世代构成新的种群
 */
public interface PopulationController {
    /**
     *
     * @param parentVariants Originals variant
     * @param childVariants New Variants
     * @param maxNumberInstances
     * @return
     */
    List<ProgramVariant> selectProgramVariantsForNextGeneration(List<ProgramVariant> parentVariants,
                                                                       List<ProgramVariant> childVariants,
                                                                       int maxNumberInstances,
                                                                       ProgramVariantFactory variantFactory,
                                                                       ProgramVariant original,
                                                                       int generation);

}
