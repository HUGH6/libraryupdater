package core.solutionsearch.spaces.operators;

import core.entity.ModificationPoint;
import core.entity.OperatorInstance;
import core.entity.ProgramVariant;
import core.migration.util.MigrationSupporter;
import core.template.diff.SimpleApiDiffer;
import core.template.diff.entity.ActionContext;
import core.template.diff.entity.ApiElement;
import core.template.diff.entity.Diff;
import core.template.diff.entity.TransferAction;
import org.apache.log4j.Logger;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtElement;

import java.util.ArrayList;
import java.util.List;

/**
 * 将对api调用点的整个转换序列的执行作为一个完整的操作符
 */
public class TemplateOperator extends IngredientBasedOperator {
    protected static final Logger logger = Logger.getLogger(TemplateOperator.class.getName());

    // 初始api
    protected ApiElement originApi = null;
    // 目标api
    protected ApiElement targetApi = null;
    // 执行代码转换的转换操作序列
    protected List<TransferAction> transferActions = new ArrayList<>();
    // 存放一些转换需要的上下文信息
    protected ActionContext context = null;

    public TemplateOperator(ApiElement originApi, ApiElement targetApi) {
        this.originApi = originApi;
        this.targetApi = targetApi;
        List<Diff> diffs = SimpleApiDiffer.diff(this.originApi, this.targetApi);
        this.transferActions.clear();
        this.transferActions.addAll(SimpleApiDiffer.getTransferActionByDiff(diffs));
        this.context = new ActionContext(originApi, targetApi);
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
        CtInvocation originElement = (CtInvocation) operatorInstance.getModificationPoint().getCodeElement();
        CtInvocation newElement = (CtInvocation) MigrationSupporter.clone(originElement);

        CtElement parent = originElement.getParent();
        boolean successful = false;

        for (TransferAction action : transferActions) {
            newElement = (CtInvocation) action.transform(newElement, context);
        }

        operatorInstance.setOriginalElement(originElement);
        operatorInstance.setModifiedElement(newElement);

        // 将变更应用到model
        operatorInstance.getModificationPoint().getCodeElement().replace(newElement);
        // 同时在modification point中进行记录
        operatorInstance.getModificationPoint().setCodeElement(newElement);

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
        // 该突变操作不需要进行撤回操作
        logger.error("template operator do not support undo change");
        return false;
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
        if (point.getCodeElement() instanceof CtInvocation) {
            return true;
        }
        return false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (TransferAction a : this.transferActions) {
            sb.append(a.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
