package core.entity;

import core.faultlocation.entity.SuspiciousCode;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtVariable;

import java.util.List;

/**
 * ModificationPoint是从可疑的代码创建的，即一个ModificationPoint是可能有bug的
 */
public class SuspiciousModificationPoint extends ModificationPoint implements Cloneable {
    // 可疑的代码
    protected SuspiciousCode suspicious;

    public SuspiciousModificationPoint() {
        super();
    }

    public SuspiciousModificationPoint(SuspiciousCode suspicious, CtElement rootElement, CtClass<?> clonedClass, List<CtVariable<?>> context) {
        super(rootElement, clonedClass, context);
        this.suspicious = suspicious;
    }

    public SuspiciousCode getSuspicious() {
        return suspicious;
    }

    public void setSuspicious(SuspiciousCode suspicious) {
        this.suspicious = suspicious;
    }

    public String toString() {
        return "MP=" + ctClass.getQualifiedName() + " line: " + suspicious.getLineNumber() + ", pointed element: "
                + codeElement.getClass().getSimpleName() + "";
    }

    @Override
    public SuspiciousModificationPoint clone() {
        SuspiciousModificationPoint smp = new SuspiciousModificationPoint(suspicious, codeElement, ctClass, contextOfModificationPoint);
        smp.identified = this.identified;
        smp.generation = this.generation;
        smp.programVariant = this.programVariant;

        return smp;
    }
}
