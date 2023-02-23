package core.template.diff.entity.action;

import core.template.diff.ApiElementBuilder;
import core.template.diff.entity.*;
import core.template.diff.entity.diff.MoveParamDiff;
import org.apache.log4j.Logger;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.Filter;

import java.util.List;

public class MoveParamAction implements TransferAction {
    private static final Logger logger = Logger.getLogger(MoveParamAction.class.getSimpleName());
    // 差异信息
    private MoveParamDiff diff = null;

    public MoveParamAction(Diff diff) {
        if (diff instanceof MoveParamDiff) {
            this.diff = (MoveParamDiff) diff;
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
            logger.warn("Invalid move param action, diff info is invalid");
            return codeElement.clone();
        }

        if (!(codeElement instanceof CtInvocation)) {
            logger.warn("Invalid move param action, code element is not CtInvocation");
            return codeElement.clone();
        }

        ParamElement paramToMove = this.diff.paramToMove;
        if (paramToMove == null
                || paramToMove.name == null
                || paramToMove.name.length() == 0
                || paramToMove.qualifiedType == null
                || paramToMove.qualifiedType.length() == 0) {
            logger.warn("Invalid move param action, param which to move is invalid");
            return codeElement.clone();
        }

        CtInvocation<?> transformedElement = (CtInvocation<?>) codeElement;

        ParamElement anchorParam = diff.moveAfter;
        if (anchorParam == null) {
            throw new IllegalStateException("illegal move position, please set move position first");
        }

        List<CtExpression<?>> arguments = transformedElement.getArguments();

        int positionOfAnchor = -1;
        int positionOfMoveParam = -1;
        for (int idx = 0; idx < context.modifiedApi.params.size(); idx++) {
            ParamElement paramInfo = context.modifiedApi.params.get(idx);
            String paramName = paramInfo.getName();
            String paramType = paramInfo.getQualifiedType();

            // 找移动锚点位置
            if (paramName.equals(anchorParam.getName()) && paramType.equals(anchorParam.getQualifiedType())) {
                positionOfAnchor = idx;
            }

            // 找被移动参数位置
            if (paramName.equals(paramToMove.getName()) && paramType.equals(paramToMove.getQualifiedType())) {
                positionOfMoveParam = idx;
            }
        }

        if (positionOfAnchor == -1 || positionOfMoveParam == -1) {
            throw new IllegalStateException("can not find anchor param or moved param");
        }

        CtExpression<?> paramToMoveExpression = arguments.get(positionOfMoveParam);
        arguments.add(positionOfAnchor + 1, paramToMoveExpression);
        context.modifiedApi.params.add(positionOfAnchor + 1, context.modifiedApi.params.get(positionOfMoveParam));
        arguments.remove(positionOfMoveParam);
        context.modifiedApi.params.remove(positionOfMoveParam);

        return transformedElement;
    }

    @Override
    public String toString() {
        return "Diff: Remove param " +this.diff.paramToMove.qualifiedType + ":"+  this.diff.paramToMove.name + " to position after " + this.diff.moveAfter.qualifiedType + ""+ this.diff.moveAfter.name;
    }

    public static void main(String[] args) {
        SpoonAPI spoon = new Launcher();
        spoon.addInputResource("E:\\projects\\migration_test_demo\\src\\main\\java");
        spoon.buildModel();

        String targetMethodName = "util.MoveParamUtil.moveParamTest";

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

        ApiElement originApi = ApiElementBuilder.buildApiElement("void util.MoveParamUtil.moveParamTest(int a,java.lang.String b, int c)");
        ApiElement targetApi = ApiElementBuilder.buildApiElement("void util.MoveParamUtil.moveParamTest(int a,int c,java.lang.String b)");

        ParamElement paramToMove = new ParamElement("java.lang.String", "b", 1);
        ParamElement paramOfAnchor = new ParamElement("int", "c", 2);
        MoveParamAction action = new MoveParamAction(new MoveParamDiff(originApi, targetApi,paramToMove, paramOfAnchor));
        ActionContext context = new ActionContext(originApi, targetApi);

        for (CtInvocation i : invocations) {
            CtElement e = action.transform(i, context);
            CtElement f = e.getPosition().getCompilationUnit();
            System.out.println(f.prettyprint());
        }
    }
}
