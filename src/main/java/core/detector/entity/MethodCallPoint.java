package core.detector.entity;

import core.detector.MethodCallDetector;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtMethod;

import java.net.URI;

/**
 * 用于表示API调用点
 */
public class MethodCallPoint {
    public URI file;                // 所在文件
    public int line;                // 代码行数
    public CtInvocation callPoint;  // 对应的方法调用AST节点

    public MethodCallPoint(CtInvocation callPoint) {
        this.file = callPoint.getPosition().getFile().toURI();
        this.line = callPoint.getPosition().getLine();
        this.callPoint = callPoint;
    }

    @Override
    public String toString() {
        return file + ":" + line + "\n" + callPoint.getParent(CtMethod.class).toString();
    }
}
