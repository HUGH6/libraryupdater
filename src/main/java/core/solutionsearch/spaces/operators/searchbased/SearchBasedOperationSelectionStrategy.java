package core.solutionsearch.spaces.operators.searchbased;

import core.entity.ModificationPoint;
import core.solutionsearch.spaces.operators.Operator;
import core.solutionsearch.spaces.operators.OperatorSelectionStrategy;
import core.solutionsearch.spaces.operators.OperatorSpace;
import util.RandomManager;

public class SearchBasedOperationSelectionStrategy extends OperatorSelectionStrategy {
    public SearchBasedOperationSelectionStrategy(OperatorSpace space) {
        super(space);
    }

    /**
     * 返回一个操作
     * 默认返回参数替换操作
     * @return
     */
    @Override
    public Operator getNextOperator() {
        Operator[] operators = getOperatorSpace().values();
        return operators[RandomManager.nextInt(operators.length)];
    }

    /**
     * 给定一个修改点，返回一个应用于该修改点的操作
     *
     * @param modificationPoint
     * @return
     */
    @Override
    public Operator getNextOperator(ModificationPoint modificationPoint) {
        // 暂时只返回参数替换操作
        return operatorSpace.getOperators().get(0);
    }
}
