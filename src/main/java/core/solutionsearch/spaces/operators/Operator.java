package core.solutionsearch.spaces.operators;

import core.entity.ModificationPoint;
import core.entity.OperatorInstance;
import core.entity.ProgramVariant;
import core.entity.SuspiciousModificationPoint;
import core.solutionsearch.population.ProgramVariantFactory;

import java.util.List;

/**
 * 表示一个用于转换代码块的操作
 * 新的操作可以通过继承该类进行实现
 */
public abstract class Operator {
    /**
     * 该方法将一个Operator的变更应用到model上（spoon模型）
     * @param operatorInstance
     * @param pv
     * @return
     */
    public abstract boolean applyChangesInModel(OperatorInstance operatorInstance, ProgramVariant pv);

    /**
     * 该方法撤销一个Operator应用的变更
     * @param operatorInstance
     * @param pv
     * @return
     */
    public abstract boolean undoChangesInModel(OperatorInstance operatorInstance, ProgramVariant pv);

    /**
     * 一些operator会从程序变体中添加或删除modification point
     * 例如，如果一个operator在t时刻移除了statement S，那么这个statment将在t+1时刻不可以被应用一个操作operator
     * @param operatorInstance
     * @param pv
     * @return
     */
    public abstract boolean updateProgramVariant(OperatorInstance operatorInstance, ProgramVariant pv);

    /**
     * 指示一个operator是否可以被应用于一个Modification Point上
     * 默认，考虑一个operator在CtStatement级别上工作
     * @param point
     * @return
     */
    public abstract boolean canBeAppliedToPoint(ModificationPoint point);

    /**
     * 创建Operator实例
     * @param modificationPoint
     * @return
     */
    public abstract List<OperatorInstance> createOperatorInstances(ModificationPoint modificationPoint);

    /**
     * 操作名称
     * @return
     */
    public String name() {
        return this.getClass().getSimpleName();
    }

    /**
     * 是否需要ingredients
     * @return
     */
    public boolean needIngredient() {
        return false;
    }

    /**
     * operators修改了程序变体的Modification Point列表
     * 该方法新建一个新Modification Point（与参数传入的Modification实例相关）
     * @param variant
     * @param operation
     * @return
     */
    protected boolean addPoint(ProgramVariant variant, OperatorInstance operation) {
        List<ModificationPoint> modifPoints = variant.getModificationPoints();

        ModificationPoint existingPoints = operation.getModificationPoint();
        ModificationPoint newPoint = null;
        if (existingPoints instanceof SuspiciousModificationPoint) {
            newPoint = ProgramVariantFactory.clonePoint((SuspiciousModificationPoint) existingPoints, operation.getModifiedElement());
        } else {
            newPoint = ProgramVariantFactory.clonePoint(existingPoints, operation.getModifiedElement());
        }

        return modifPoints.add(newPoint);
    }

    /**
     * operators修改了程序变体的Modification Point列表
     * 该方法移除一个新Modification Point（与参数传入的Modification实例相关）
     * @param variant
     * @param operation
     * @return
     */
    protected boolean removePoint(ProgramVariant variant, OperatorInstance operation) {
        List<ModificationPoint> modifPoints = variant.getModificationPoints();
        boolean removed = modifPoints.remove(operation.getModificationPoint());
        return removed;
    }

    public String toString() {
        return this.name();
    }

    public abstract OperatorInstance createOperatorInstance(ModificationPoint modificationPoint);
}