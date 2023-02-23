package core.solutionsearch.spaces.operators;

import core.entity.ModificationPoint;
import core.entity.OperatorInstance;
import core.entity.ProgramVariant;

import java.util.List;

public class NormalizeOperator extends Operator {
    /**
     * 该方法将一个Operator的变更应用到model上（spoon模型）
     *
     * @param operatorInstance
     * @param pv
     * @return
     */
    @Override
    public boolean applyChangesInModel(OperatorInstance operatorInstance, ProgramVariant pv) {
        return false;
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
        return false;
    }

    /**
     * 创建Operator实例
     *
     * @param modificationPoint
     * @return
     */
    @Override
    public List<OperatorInstance> createOperatorInstances(ModificationPoint modificationPoint) {
        return null;
    }

    @Override
    public OperatorInstance createOperatorInstance(ModificationPoint modificationPoint) {
        return null;
    }
}
