package core.template.diff.entity.action;

import core.migration.util.MigrationSupporter;
import core.template.diff.ApiElementBuilder;
import core.template.diff.entity.*;
import core.template.diff.entity.diff.AddParamDiff;
import org.apache.log4j.Logger;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.visitor.Filter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddParamAction implements TransferAction {
    private static final Logger logger = Logger.getLogger(AddParamAction.class.getSimpleName());
    // 差异信息
    private AddParamDiff diff = null;
    // 存放变量计数
    private Map<String, Integer> varCount = new HashMap<>();

    public AddParamAction(Diff diff) {
        if (diff instanceof AddParamDiff) {
            this.diff = (AddParamDiff) diff;
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
            logger.warn("Invalid add param action, diff info is invalid");
            return codeElement;
        }

        if (!(codeElement instanceof CtInvocation)) {
            logger.warn("Invalid add param action, code element is not CtInvocation");
            return codeElement;
        }

        ParamElement paramToAdd = this.diff.paramToAdd;
        if (paramToAdd == null
                || paramToAdd.name == null
                || paramToAdd.name.length() == 0
                || paramToAdd.qualifiedType == null
                || paramToAdd.qualifiedType.length() == 0) {
            logger.warn("Invalid add param action, param which to move is invalid");
            return codeElement;
        }

        CtInvocation transformedElement = (CtInvocation) codeElement;

        ParamElement anchorParam = diff.addAfter;

        // 创建一个局部变量
        CtCodeSnippetStatement stat = transformedElement.getFactory().createCodeSnippetStatement();
        CtVariable var = createLocalVariable(paramToAdd);
        stat.setValue(var.toString());


        // 有一点问题
        transformedElement.getParent(CtStatement.class).insertBefore(stat);

        List<CtExpression<?>> arguments = transformedElement.getArguments();

        int positionToAdd = -1;
        if (anchorParam == null) {
            // 添加在第一位
            positionToAdd = 0;
        } else {
            // 添加在锚点前
            int positionOfAnchor = -1;
            for (int idx = 0; idx < context.modifiedApi.params.size(); idx++) {
                ParamElement paramInfo = context.modifiedApi.params.get(idx);
                String paramName = paramInfo.getName();
                String paramType = paramInfo.getQualifiedType();

                // 找移动锚点位置
                if (paramName.equals(anchorParam.getName()) && paramType.equals(anchorParam.getQualifiedType())) {
                    positionOfAnchor = idx;
                }
            }

            if (positionOfAnchor == -1) {
                throw new IllegalStateException("can not find anchor param or moved param");
            }

            positionToAdd = positionOfAnchor;
        }

        CtVariableRead varRead = transformedElement.getFactory().createVariableRead();
        varRead.setVariable(var.getReference());

        arguments.add(positionToAdd + 1, varRead);
        context.modifiedApi.params.add(positionToAdd + 1, paramToAdd.clone());

        return transformedElement;
    }

    /**
     * 根据给定的方法调用实际参数表达式生成一个对应的局部变量生命表达式
     * @param paramToAdd 方法调用中的参数表达式
     * @return 对应的局部变量声明语句
     */
    private CtLocalVariable createLocalVariable(ParamElement paramToAdd) {
        CtLocalVariable var = MigrationSupporter.getFactory().createLocalVariable();
        int varTypeCount = varCount.getOrDefault(paramToAdd.qualifiedType, 0);

        var.setSimpleName(generateVarName(paramToAdd.qualifiedType, varTypeCount));
        var.setType(MigrationSupporter.getFactory().Type().createReference(paramToAdd.qualifiedType));
        var.setAssignment(getDefaultValue(paramToAdd.qualifiedType));

        varCount.put(paramToAdd.qualifiedType, varTypeCount + 1);

        return var;
    }

    private CtExpression<?> getDefaultValue(String varType) {
        CtExpression<?> defaultValue = null;
        if ("int".equals(varType) || "java.lang.Integer".equals(varType)) {
            defaultValue = MigrationSupporter.getFactory().createCodeSnippetExpression("0");
        } else if ("double".equals(varType) || "java.lang.Double".equals(varType)) {
            defaultValue = MigrationSupporter.getFactory().createCodeSnippetExpression("0.0");
        } else if ("boolean".equals(varType) || "java.lang.Boolean".equals(varType)) {
            defaultValue = MigrationSupporter.getFactory().createCodeSnippetExpression("false");
        } else if ("java.lang.String".equals(varType)) {
            defaultValue = MigrationSupporter.getFactory().createCodeSnippetExpression("");
        } else if ("float".equals(varType) || "java.lang.Float".equals(varType)) {
            defaultValue = MigrationSupporter.getFactory().createCodeSnippetExpression("0.0f");
        } else if ("long".equals(varType) || "java.lang.Long".equals(varType)) {
            defaultValue = MigrationSupporter.getFactory().createCodeSnippetExpression("0L");
        } else {
            defaultValue = MigrationSupporter.getFactory().createCodeSnippetExpression("new " + varType + "()");
        }
        return defaultValue;
    }

    /**
     * 按特定规则生成局部变量名称
     * @param varType 局部变量类型
     * @param varTypeCount 当前作用域该类型变量数量
     * @return 变量名称
     */
    private String generateVarName(String varType, int varTypeCount) {
        return "$" + varType + "_var" + varTypeCount;
    }

    @Override
    public String toString() {
        return "Diff: Add param " + this.diff.paramToAdd + " after param " + this.diff.addAfter;
    }

    public static void main(String[] args) {
        SpoonAPI spoon = new Launcher();
        spoon.addInputResource("E:\\projects\\migration_test_demo\\src\\main\\java");
        spoon.buildModel();

        String targetMethodName = "util.AddParamUtil.function1";

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

        ApiElement originApi = ApiElementBuilder.buildApiElement("int util.AddParamUtil.function1(int a)");
        ApiElement targetApi = ApiElementBuilder.buildApiElement("int util.AddParamUtil.function1(int a,int b)");

        AddParamDiff diff = new AddParamDiff(originApi, targetApi, new ParamElement("int", "b", 1), new ParamElement("int", "a", 0));
        AddParamAction action = new AddParamAction(diff);
        ActionContext context = new ActionContext(originApi, targetApi);
        for (CtInvocation i : invocations) {
            CtElement e = action.transform(i, context);
            System.out.println(e.getPosition().getCompilationUnit().prettyprint());
        }
    }
}
