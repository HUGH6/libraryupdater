package core.template.diff.entity.action;

import core.template.diff.ApiElementBuilder;
import core.template.diff.entity.ActionContext;
import core.template.diff.entity.ApiElement;
import core.template.diff.entity.Diff;
import core.template.diff.entity.TransferAction;
import core.template.diff.entity.diff.RemoveExceptionDiff;
import org.apache.log4j.Logger;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.Filter;

import java.util.List;

public class RemoveExceptionAction implements TransferAction {
    private static final Logger logger = Logger.getLogger(RemoveExceptionAction.class.getName());
    // 差异信息
    private RemoveExceptionDiff diff = null;

    public RemoveExceptionAction(Diff diff) {
        if (diff instanceof RemoveExceptionDiff) {
            this.diff = (RemoveExceptionDiff) diff;
        }
    }

    /**
     * 在代码元素上应用转换
     *
     * @param codeElement
     * @return 转换后的代码元素
     */
    @Override
    public CtElement transform(CtElement codeElement, ActionContext context) {
        if (this.diff == null) {
            logger.warn("Invalid remove exception action, diff info is invalid");
            return codeElement;
        }

        if (!(codeElement instanceof CtInvocation)) {
            logger.warn("Invalid remove exception action, code element is not CtInvocation");
            return codeElement;
        }

        if (this.diff.exceptionToRemove == null || "".equals(this.diff.exceptionToRemove)) {
            logger.warn("Invalid remove exception action, diff info is invalid");
            return codeElement;
        }

        // 方法调用
        CtInvocation<?> transformedElement = (CtInvocation<?>) codeElement;

        // 尝试获取是否有try语句
        CtTry ct = transformedElement.getParent(CtTry.class);
        if (ct == null) {
            // 若没有try语句，则无需处理
            return transformedElement;
        }

        List<CtCatch> catches = ct.getCatchers();
        CtCatch catchToRemove = null;
        for (CtCatch c : catches) {
            String paramQualifiedName = c.getParameter().getType().getQualifiedName();
            if (paramQualifiedName.equals(this.diff.exceptionToRemove)) {
                catchToRemove = c;
                break;
            }
        }

        // 没有对应的异常catch，无需处理
        if (catchToRemove == null) {
            return transformedElement;
        }

        // 移除catch块
        ct.removeCatcher(catchToRemove);

        // 若try后没有catch块，则移除try块
        if (ct.getCatchers().size() == 0) {
            CtBlock body = ct.getBody();
            for (CtStatement s : body.getStatements()) {
                s.setParent(ct.getParent());
                ct.insertBefore(s);
            }
            ct.delete();
        }

        return transformedElement;
    }

    public static void main(String[] args) {
        SpoonAPI spoon = new Launcher();
        spoon.addInputResource("E:\\projects\\migration_test_demo\\src\\main\\java");
        spoon.buildModel();

        String targetMethodName = "util.RemoveExceptionUtil.removeExceptionTest";

        List<CtInvocation> invocations = spoon.getModel().getElements((Filter<CtInvocation>) element -> {
            String classOfMethod = element.getExecutable().getDeclaringType().getQualifiedName();
            String methodSignature = element.getExecutable().getSignature();
            String methodName = methodSignature.substring(0, methodSignature.indexOf('('));
            String fullMethodName = classOfMethod + "." + methodName;

            // 返回值类型和方法全限定签名相同
            if (targetMethodName.equals(fullMethodName)) {
                return true;
            }

            return false;
        });

        ApiElement originApi = ApiElementBuilder.buildApiElement("int util.RemoveExceptionUtil.removeExceptionTest() throws java.io.IOException");
        ApiElement targetApi = ApiElementBuilder.buildApiElement("int util.RemoveExceptionUtil.removeExceptionTest()");

        RemoveExceptionDiff diff = new RemoveExceptionDiff(originApi, targetApi,"java.io.IOException");
        RemoveExceptionAction action = new RemoveExceptionAction(diff);
        ActionContext context = new ActionContext(originApi, targetApi);

        for (CtInvocation i : invocations) {
            CtElement e = action.transform(i, context);
            CtElement f = e.getPosition().getCompilationUnit();
            System.out.println(f.prettyprint());
        }
    }

    @Override
    public String toString() {
        return "Diff: Remove exception " + this.diff.exceptionToRemove;
    }
}
