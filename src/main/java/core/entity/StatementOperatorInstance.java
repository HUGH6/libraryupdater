package core.entity;

import core.solutionsearch.spaces.operators.Operator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;

/**
 * 对statement进行的操作实例
 */
public class StatementOperatorInstance extends OperatorInstance {
    // statement的父block对象
    private CtBlock<?> parentBlock = null;
    // parent block是否是隐形的
    private boolean isParentBlockImplicit = false;
    // 在父block中的位置
    private int locationInParentBlock = -1;

    public StatementOperatorInstance() {
        super();
    }

    public StatementOperatorInstance(ModificationPoint modificationPoint, Operator operationApplied,
                                     CtElement originalElement, CtElement modifiedElement) {
        super(modificationPoint, operationApplied, originalElement, modifiedElement);
        this.defineParentInfomation(modificationPoint);
    }

    public StatementOperatorInstance(ModificationPoint modificationPoint, Operator operationApplied,
                                     CtElement modifiedElement) {
        this(modificationPoint, operationApplied, modificationPoint.getCodeElement(), modifiedElement);
    }

    /**
     * 获取element在父block中的位置
     * @param parentBlock
     * @param element
     * @return
     */
    public int elementLocationInParent(CtBlock parentBlock, CtElement element) {
        int pos = 0;
        for (CtStatement s : parentBlock.getStatements()) {
            if (s == element) {
                return pos;
            }
            pos++;
        }
        return -1;
    }

    public boolean defineParentInfomation(ModificationPoint modificationPoint) {
        CtElement targetStmt = modificationPoint.getCodeElement();
        CtElement parent = targetStmt.getParent();
        if ((parent != null && (parent instanceof CtBlock))) {
            CtBlock parentBlock = (CtBlock) parent;
            int location = elementLocationInParent(parentBlock, targetStmt);
            if (location >= 0) {
                this.setParentBlock(parentBlock);
                this.setLocationInParentBlock(location);
                return true;
            }
        } else {
            logger.error("Parent null or it is not a block");
        }
        return false;
    }

    /***************************************
     *  getter and setter
     **************************************/
    public CtBlock<?> getParentBlock() {
        return parentBlock;
    }

    public void setParentBlock(CtBlock<?> parentBlock) {
        this.parentBlock = parentBlock;
        this.isParentBlockImplicit = parentBlock.isImplicit();
    }

    public int getLocationInParentBlock() {
        return locationInParentBlock;
    }

    public void setLocationInParentBlock(int locationInParentBlock) {
        this.locationInParentBlock = locationInParentBlock;
    }

    public boolean isParentBlockImplicit() {
        return isParentBlockImplicit;
    }

    public void setParentBlockImplicit(boolean parentBlockImplicit) {
        isParentBlockImplicit = parentBlockImplicit;
    }
}
