package core.template.diff.entity.action;

import core.template.diff.ApiElementBuilder;
import core.template.diff.entity.ActionContext;
import core.template.diff.entity.ApiElement;
import core.template.diff.entity.Diff;
import core.template.diff.entity.TransferAction;
import core.template.diff.entity.diff.ReturnTypeChangeDiff;
import org.apache.log4j.Logger;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Filter;

import java.util.Arrays;
import java.util.List;

public class ReturnTypeChangeAction implements TransferAction {
    private static final Logger logger = Logger.getLogger(ReturnTypeChangeAction.class.getName());
    // 差异信息
    private ReturnTypeChangeDiff diff = null;

    public ReturnTypeChangeAction(Diff diff) {
        if (diff instanceof ReturnTypeChangeDiff) {
            this.diff = (ReturnTypeChangeDiff) diff;
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
            logger.warn("Invalid return type change action, diff info is invalid");
            return codeElement;
        }

        if (!(codeElement instanceof CtInvocation)) {
            logger.warn("Invalid return type change action, code element is not CtInvocation");
            return codeElement;
        }

        String originType = this.diff.originReturnType;
        String targetType = this.diff.targetReturnType;

        if ((originType == null || originType.length() == 0) || (targetType == null || targetType.length() == 0)) {
            logger.warn("Invalid return type change action, origin name or target name is empty");
            return codeElement;
        }

        CtInvocation<?> transformedElement = (CtInvocation<?>) codeElement;

        CtTypeReference reference = transformedElement.getFactory().Type().createReference(targetType);
        transformedElement.setTypeCasts(Arrays.asList(reference));

        return transformedElement;
    }

    public static void main(String[] args) {
        SpoonAPI spoon = new Launcher();
        spoon.addInputResource("E:\\projects\\migration_test_demo\\src\\main\\java");
        spoon.buildModel();

        String targetMethodName = "util.MathUtil2.getResObj";

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

        ApiElement originApi = ApiElementBuilder.buildApiElement("int util.MathUtil.sum(int a,int b)");
        ApiElement targetApi = ApiElementBuilder.buildApiElement("java.lang.String util.MathUtil.sum(int a)");

        ReturnTypeChangeDiff diff = new ReturnTypeChangeDiff(originApi, targetApi,"int", "java.lang.String");
        ReturnTypeChangeAction action = new ReturnTypeChangeAction(diff);
        ActionContext context = new ActionContext(originApi, targetApi);
        for (CtInvocation i : invocations) {
            CtElement e = action.transform(i, context);
//            System.out.println(e.getPosition().getCompilationUnit().prettyprint());
        }
    }

    @Override
    public String toString() {
        return "Diff: Change return type from " + this.diff.originReturnType + " to " + this.diff.targetReturnType;
    }
}
