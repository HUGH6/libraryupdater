package core.template.diff.entity.action;

import core.template.diff.ApiElementBuilder;
import core.template.diff.entity.*;
import core.template.diff.entity.diff.RemoveParamDiff;
import org.apache.log4j.Logger;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.Filter;

import java.util.List;

public class RemoveParamAction implements TransferAction {
    private static final Logger logger = Logger.getLogger(RemoveParamAction.class.getSimpleName());
    // 差异信息
    private RemoveParamDiff diff = null;

    public RemoveParamAction(Diff diff) {
        if (diff instanceof RemoveParamDiff) {
            this.diff = (RemoveParamDiff) diff;
        }
    }

    /**
     * 在代码元素上应用转换，若转换无效，则返回一个没有做修改的克隆对象
     * @param codeElement 待修改的代码对象
     * @return 转换后的代码元素，若转换无效，则返回一个没有做修改的克隆对象
     */
    @Override
    public CtElement transform(CtElement codeElement, ActionContext context) {
        if (this.diff == null) {
            logger.warn("Invalid remove param action, diff info is invalid");
            return codeElement;
        }

        if (!(codeElement instanceof CtInvocation)) {
            logger.warn("Invalid remove param action, code element is not CtInvocation");
            return codeElement;
        }

        ParamElement paramToRemove = this.diff.paramToRemove;
        if (paramToRemove == null
                || paramToRemove.name == null
                || paramToRemove.name.length() == 0
                || paramToRemove.qualifiedType == null
                || paramToRemove.qualifiedType.length() == 0) {
            logger.warn("Invalid remove param action, param which to remove is invalid");
            return codeElement;
        }

        CtInvocation<?> transformedElement = (CtInvocation<?>) codeElement;

        List<CtExpression<?>> arguments = transformedElement.getArguments();
        int positionToRemove = -1;
        for (int idx = 0; idx < context.modifiedApi.params.size(); idx++) {
            ParamElement paramInfo = context.modifiedApi.params.get(idx);
            String paramName = paramInfo.getName();
            String paramType = paramInfo.getQualifiedType();

            if (paramName.equals(paramToRemove.getName())
                    && paramType.equals(paramToRemove.getQualifiedType())) {
                positionToRemove = idx;
                break;
            }
        }

        if (positionToRemove != -1) {
            // 需要保持context中的参数列表与当前调用点的参数列表信息一致
            arguments.remove(positionToRemove);
            context.modifiedApi.params.remove(positionToRemove);
        }

        return transformedElement;
    }

    public static void main(String[] args) {
        SpoonAPI spoon = new Launcher();
        spoon.addInputResource("E:\\projects\\migration_test_demo\\src\\main\\java");
        spoon.buildModel();

        String targetMethodName = "util.RemoveParamUtil.removeTest";

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

        ApiElement originApi = ApiElementBuilder.buildApiElement("void util.RemoveParamUtil.removeTest(int a,int b)");
        ApiElement targetApi = ApiElementBuilder.buildApiElement("void util.RemoveParamUtil.removeTest(int a)");

        ParamElement paramToRemove = new ParamElement("int", "b", 1);
        RemoveParamAction action = new RemoveParamAction(new RemoveParamDiff(originApi, targetApi,paramToRemove));
        ActionContext context = new ActionContext(originApi, targetApi);

        for (CtInvocation i : invocations) {
            CtElement e = action.transform(i, context);
            CtElement f = e.getParent(CtMethod.class);
            System.out.println(f.prettyprint());
        }
    }

    @Override
    public String toString() {
        return "Diff: remove param " + this.diff.paramToRemove;
    }
}