package core.solutionsearch.population;

import core.entity.ProgramVariant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 基于单元测试执行结果计算的适应度的种群筛选控制器
 */
public class TestCaseFitnessBasedPopulationController implements PopulationController{

    public FitnessComparator comparator = new FitnessComparator();

    /**
     * @param parentVariants     Originals variant
     * @param childVariants      New Variants
     * @param maxNumberInstances
     * @param variantFactory
     * @param original
     * @param generation
     * @return
     */
    @Override
    public List<ProgramVariant> selectProgramVariantsForNextGeneration(List<ProgramVariant> parentVariants,
                                                                       List<ProgramVariant> childVariants,
                                                                       int maxNumberInstances,
                                                                       ProgramVariantFactory variantFactory,
                                                                       ProgramVariant original,
                                                                       int generation) {
        // 从新生成的种群来作为初始的下一代种群
        List<ProgramVariant> newPopulation = new ArrayList<>(childVariants);

        // 基于适应度排序
        Collections.sort(newPopulation, comparator);

        // 提出适应度最大的变体，即那些编译失败的个体
        newPopulation = newPopulation.stream()
                .filter(v -> !(v.getFitness() >= Double.MAX_VALUE) )
                .collect(Collectors.toList());

        // 选择适应度最好的前x位
        int min = (newPopulation.size() > maxNumberInstances) ? maxNumberInstances : newPopulation.size();
        newPopulation = newPopulation.subList(0, min);

        // 当新时代变体数量少于设定值时，则重复添加初始变体，直到达到预定数量
        while (newPopulation.size() < maxNumberInstances) {
            ProgramVariant originalVariant = variantFactory.createProgramVariantFromAnother(original, generation);
            originalVariant.setParent(null);
            newPopulation.add(originalVariant);
        }

        return newPopulation;
    }

    /**
     * 基于程序变体适应度值从小到达排序的比较器
     * @author Matias Martinez, matias.martinez@inria.fr
     *
     */
    public class FitnessComparator implements Comparator<ProgramVariant> {
        @Override
        public int compare(ProgramVariant o1, ProgramVariant o2) {
            int fitness = Double.compare(o1.getFitness(), o2.getFitness());
            if (fitness != 0) {
                return fitness;
            }
            // 当适应度相同时，倾向与选择有更多子变体的程序变体
            return Integer.compare(o1.getId(), o2.getId());
        }

    }
}
