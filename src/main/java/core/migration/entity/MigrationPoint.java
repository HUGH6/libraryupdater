package core.migration.entity;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;

/**
 * 表示一个当前被分析代码中一个用于执行迁移操作的代码实体
 */
public class MigrationPoint {
    // 对应的代码实体
    protected CtElement codeElement;
    // 所属的类对象
    protected CtClass<?> ctClass;

    /*************************************
     * getter and setter
     ************************************/
    public CtElement getCodeElement() {
        return codeElement;
    }

    public void setCodeElement(CtElement codeElement) {
        this.codeElement = codeElement;
    }

    public CtClass<?> getCtClass() {
        return ctClass;
    }

    public void setCtClass(CtClass<?> ctClass) {
        this.ctClass = ctClass;
    }
}
