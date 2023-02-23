package core.entity;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtVariable;

import java.util.List;

/**
 * 程序变体的修改点，表示当前分析的程序中的一个元素（即spoon元素，如CtElement）
 */
public class ModificationPoint implements Comparable<ModificationPoint>, Cloneable{
    // 所属的程序变体
    protected ProgramVariant programVariant;
    // 对应的spoon程序元素
    protected CtElement codeElement;
    // 程序元素所属的类
    protected CtClass<?> ctClass;
    // 修改点的上下文变量
    List<CtVariable<?>> contextOfModificationPoint;
    // 标识id
    public int identified = 0;
    // 所属代数
    protected int generation = -1;

    public ModificationPoint() {
    }

    public ModificationPoint(int id, CtElement rootElement, CtClass<?> ctClass, List<CtVariable<?>> contextOfGen, int generation) {
        super();
        this.identified = id;
        this.codeElement = rootElement;
        this.ctClass = ctClass;
        this.contextOfModificationPoint = contextOfGen;
        this.generation = generation;
    }

    public ModificationPoint(CtElement rootElement, CtClass<?> ctClass, List<CtVariable<?>> contextOfGen) {
        super();
        this.codeElement = rootElement;
        this.ctClass = ctClass;
        this.contextOfModificationPoint = contextOfGen;
    }

    public CtElement getCodeElement() {
        return codeElement;
    }

    public void setCodeElement(CtElement rootElement) {
        this.codeElement = rootElement;
    }

    public CtClass<?> getCtClass() {
        return ctClass;
    }

    public void setCtClass(CtClass<?> clonedClass) {
        this.ctClass = clonedClass;
    }

    public String toString() {
        return "[" + codeElement.getClass().getSimpleName() + ", in " + ctClass.getQualifiedName() + "]";
    }

    public List<CtVariable<?>> getContextOfModificationPoint() {
        return contextOfModificationPoint;
    }

    public void setContextOfModificationPoint(List<CtVariable<?>> contextOfModificationPoint) {
        this.contextOfModificationPoint = contextOfModificationPoint;
    }

    public ProgramVariant getProgramVariant() {
        return programVariant;
    }

    public void setProgramVariant(ProgramVariant programVariant) {
        this.programVariant = programVariant;
    }

    @Override
    public int compareTo(ModificationPoint o) {
        if (o instanceof ModificationPoint) {
            return Integer.compare(this.identified, o.identified);
        }
        return 0;
    }

    @Override
    public ModificationPoint clone() {
        return new ModificationPoint(identified, codeElement, ctClass, contextOfModificationPoint, generation);
    }
}
