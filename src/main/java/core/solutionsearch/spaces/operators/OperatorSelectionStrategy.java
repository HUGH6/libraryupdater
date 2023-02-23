package core.solutionsearch.spaces.operators;

import core.entity.ModificationPoint;

/**
 * 表示在一个操作空间中选择操作的策略
 */
public abstract class OperatorSelectionStrategy {
    protected OperatorSpace operatorSpace;

    public OperatorSelectionStrategy(OperatorSpace space) {
        super();
        this.operatorSpace = space;
    }

    /**
     * 返回一个操作
     * @return
     */
    public abstract Operator getNextOperator();

    /**
     * 给定一个修改点，返回一个应用于该修改点的操作
     * @param modificationPoint
     * @return
     */
    public abstract Operator getNextOperator(ModificationPoint modificationPoint);

    /**
     * 获取操作空间
     * @return
     */
    public OperatorSpace getOperatorSpace() {
        return this.operatorSpace;
    }
}
