package core.solutionsearch.spaces.operators.searchbased;

import core.entity.ModificationPoint;
import core.entity.OperatorInstance;
import core.entity.ProgramVariant;
import core.ingredient.Ingredient;
import core.ingredient.IngredientPool;
import core.ingredient.extract.callpoint.CallPointIngredientExtractStrategy;
import core.migration.util.MigrationSupporter;
import core.solutionsearch.spaces.operators.Operator;
import core.template.diff.entity.ApiElement;
import core.template.diff.entity.ParamElement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import util.RandomManager;

import java.util.List;

/**
 * 表示一个替换参数的突变操作
 */
public class ParamReplaceOperator extends Operator {
    // 初始api
    protected ApiElement originApi = null;
    // 目标api
    protected ApiElement targetApi = null;
    // 用于替换的材料
    private Ingredient selectedIngredient;
    // 指定进行替换的参数
    private ParamElement param;
    // 材料池
    private IngredientPool ingredientPool;

    public ParamReplaceOperator(ApiElement originApi, ApiElement targetApi) {
        this.originApi = originApi;
        this.targetApi = targetApi;
    }

    /**
     * 该方法将一个Operator的变更应用到model上（spoon模型）
     *
     * @param operatorInstance
     * @param pv
     * @return
     */
    @Override
    public boolean applyChangesInModel(OperatorInstance operatorInstance, ProgramVariant pv) {
        boolean successful = false;
        if (ingredientPool == null) {
            this.ingredientPool = CallPointIngredientExtractStrategy.extractIngredient((CtInvocation) operatorInstance.getOriginalElement(), targetApi);
        }
        if (targetApi.params.size() == 0) {
            return successful;
        }

        CtInvocation originInvocation = (CtInvocation) operatorInstance.getModificationPoint().getCodeElement();
        CtInvocation originalElementCloned = (CtInvocation) MigrationSupporter.clone(originInvocation);

        // 随机选择一个参数
        this.param = targetApi.params.get(RandomManager.nextInt(targetApi.params.size() - 1));

        // 选择一个与参数名称编辑距离最接近的变量
        List<Ingredient> ingredients = this.ingredientPool.getIngredients().get(this.param.qualifiedType);
        this.selectedIngredient = ingredients.get(RandomManager.nextInt(ingredients.size()));

        int paramIndex = targetApi.params.indexOf(this.param);
        List<CtExpression<?>> arguments = originalElementCloned.getArguments();
        CtExpression expression = originalElementCloned.getFactory().createCodeSnippetExpression(this.selectedIngredient.getCode().toString());
        arguments.set(paramIndex, expression);

        // 保存初始代码元素
        operatorInstance.setOriginalElement(originInvocation);
        // 保存转换后的代码元素
        operatorInstance.setModifiedElement(originalElementCloned);
        // 将变更应用到model
        operatorInstance.getOriginalElement().replace(originalElementCloned);
//        operatorInstance.getModificationPoint().getCodeElement().replace(originalElementCloned);
        // 更新modification point中的代码元素为更新后的元素
        operatorInstance.getModificationPoint().setCodeElement(originalElementCloned);

        successful = true;
        operatorInstance.setSuccessfullyApplied(true);

        return successful;
    }

    /**
     * 该方法撤销一个Operator应用的变更
     *
     * @param operatorInstance
     * @param pv
     * @return
     */
    @Override
    public boolean undoChangesInModel(OperatorInstance operatorInstance, ProgramVariant pv) {
        // 将spoon model中的组件替换为转换前的组件,恢复spoon model
        operatorInstance.getModificationPoint().getCodeElement().replace(operatorInstance.getOriginalElement());
        // 将modification point中记录的当前代码元素也还原为转换前的组件
        operatorInstance.getModificationPoint().setCodeElement(operatorInstance.getOriginalElement());
        return true;
    }

    /**
     * 一些operator会从程序变体中添加或删除modification point
     * 例如，如果一个operator在t时刻移除了statement S，那么这个statment将在t+1时刻不可以被应用一个操作operator
     *
     * @param operatorInstance
     * @param pv
     * @return
     */
    @Override
    public boolean updateProgramVariant(OperatorInstance operatorInstance, ProgramVariant pv) {
        return false;
    }

    /**
     * 指示一个operator是否可以被应用于一个Modification Point上
     * 默认，考虑一个operator在CtStatement级别上工作
     *
     * @param point
     * @return
     */
    @Override
    public boolean canBeAppliedToPoint(ModificationPoint point) {
        return (point.getCodeElement() instanceof CtInvocation);
    }

    /**
     * 创建Operator实例
     *
     * @param modificationPoint
     * @return
     */
    @Override
    public List<OperatorInstance> createOperatorInstances(ModificationPoint modificationPoint) {
//        OperatorInstance instance = new OperatorInstance(modificationPoint, this, modificationPoint.getCodeElement(), null);
//        return instance;
        return null;
    }

    public OperatorInstance createOperatorInstance(ModificationPoint modificationPoint) {
        OperatorInstance instance = new OperatorInstance(modificationPoint, this, modificationPoint.getCodeElement(), null);
        return instance;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("replace " + this.param + " to " + this.selectedIngredient.getCode().toString());
        return sb.toString();
    }
}
