package core.entity;

import core.ingredient.Ingredient;
import core.solutionsearch.spaces.operators.Operator;
import org.apache.log4j.Logger;
import spoon.reflect.declaration.CtElement;

/**
 * 应用在一个modification point上的操作
 */
public class OperatorInstance {
    protected static final Logger logger = Logger.getLogger(OperatorInstance.class.getName());

    // 应用操作的修改点
    private ModificationPoint modificationPoint = null;
    // 操作应用的原始元素
    private CtElement originalElement = null;
    // 修改后的新元素
    private CtElement modifiedElement = null;
    // 应用的操作种类
    private Operator operator = null;
    // 操作是否应用成功
    private boolean successfullyApplied = true;
    // 修复材料（部分需要材料的转换操作会使用到）
    private Ingredient ingredient = null;

    public OperatorInstance() {}

    /**
     * Creates a modification instance
     *
     * @param modificationPoint
     * @param operationApplied
     * @param original
     * @param modified
     */
    public OperatorInstance(ModificationPoint modificationPoint, Operator operationApplied, CtElement original,
                            CtElement modified) {
        super();
        this.modificationPoint = modificationPoint;
        this.operator = operationApplied;
        this.originalElement = original;
        this.modifiedElement = modified;
    }

    /****************************************
     * getter and setter
     ***************************************/

     public CtElement getOriginalElement() {
        return originalElement;
    }

    public void setOriginalElement(CtElement original) {
        this.originalElement = original;
    }

    public CtElement getModifiedElement() {
        return modifiedElement;
    }

    public void setModifiedElement(CtElement modified) {
        this.modifiedElement = modified;
    }

    public Operator getOperationApplied() {
        return operator;
    }

    public void setOperationApplied(Operator operationApplied) {
        this.operator = operationApplied;
    }

    public ModificationPoint getModificationPoint() {
        return modificationPoint;
    }

    public void setModificationPoint(ModificationPoint modificationPoint) {
        this.modificationPoint = modificationPoint;
    }

    public boolean isSuccessfullyApplied() {
        return this.successfullyApplied;
    }

    public void setSuccessfullyApplied(boolean successfullyApplied) {
        this.successfullyApplied = successfullyApplied;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((modificationPoint == null) ? 0 : modificationPoint.hashCode());
        result = prime * result + ((modifiedElement == null) ? 0 : modifiedElement.hashCode());
        result = prime * result + ((operator == null) ? 0 : operator.hashCode());
        result = prime * result + ((originalElement == null) ? 0 : originalElement.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        OperatorInstance other = (OperatorInstance) obj;
        if (modificationPoint == null) {
            if (other.modificationPoint != null) {
                return false;
            }
        } else if (!modificationPoint.equals(other.modificationPoint)) {
            return false;

        }
        if (modifiedElement == null) {
            if (other.modifiedElement != null) {
                return false;
            }
        } else if (!modifiedElement.equals(other.modifiedElement)) {
            return false;
        }
        if (operator == null) {
            if (other.operator != null) {
                return false;
            }
        } else if (!operator.equals(other.operator)) {
            return false;
        }
        if (originalElement == null) {
            if (other.originalElement != null) {
                return false;
            }
        } else if (!originalElement.equals(other.originalElement)) {
            return false;
        }
        return true;
    }

    public boolean apply() {
        return operator.applyChangesInModel(this, this.getModificationPoint().getProgramVariant());
    }

    public boolean undo() {
        return operator.undoChangesInModel(this, this.getModificationPoint().getProgramVariant());
    }

    public void updateProgramVariant() {
        operator.updateProgramVariant(this, this.getModificationPoint().getProgramVariant());
    }

    public String toString() {
         return this.operator.toString();
    }
}
