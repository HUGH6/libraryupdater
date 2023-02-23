package core.solutionsearch.spaces.operators;

import core.entity.ModificationPoint;
import core.entity.OperatorInstance;
import core.ingredient.Ingredient;

import java.util.List;

/**
 * 依赖代码修复材料的转换操作
 */
public abstract class IngredientBasedOperator extends Operator {
    @Override
    public boolean needIngredient() {
        return true;
    }

    protected OperatorInstance createOperatorInstance(ModificationPoint mp, Ingredient ingredient) {
        OperatorInstance oi = this.createOperatorInstance(mp);
        oi.setModifiedElement(ingredient.getCode());
        oi.setIngredient(ingredient);
        return oi;
    }

    /**
     * 创建一个对modification point的操作实例
     * @param mp
     * @return
     */
    public OperatorInstance createOperatorInstance(ModificationPoint mp) {
        OperatorInstance oi = new OperatorInstance();
        oi.setOriginalElement(mp.getCodeElement());
        oi.setOperationApplied(this);
        oi.setModificationPoint(mp);
        return oi;
    }

    @Override
    public List<OperatorInstance> createOperatorInstances(ModificationPoint modificationPoint) {
        throw new IllegalAccessError("an ingredient-based operator needs an ingredient, this method could never be called");
    }
}
