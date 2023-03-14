package core.template.diff.entity.action;

import core.template.diff.entity.ActionContext;
import core.template.diff.entity.TransferAction;
import org.apache.log4j.Logger;
import spoon.Launcher;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.visitor.Filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 该类用于对方法调用参数进行规范化转换，即将方法调用中的实际参数值提取到外部，以局部变量的形式表示
 */
public class InvocationNormalizeAction implements TransferAction {
    private static final Logger logger = Logger.getLogger(InvocationNormalizeAction.class.getSimpleName());
    private Map<String, Integer> varCount = new HashMap<>();

    /**
     * 将方法调用的实际参数提取到方法调用前，用局部变量表示
     * @param codeElement 进行转换的方法调用元素
     * @return 转换后的代码元素
     */
    @Override
    public CtElement transform(CtElement codeElement, ActionContext context) {
        if (codeElement == null || !(codeElement instanceof CtInvocation)) {
            logger.warn("Invalid invocation normalize action, code element invalid");
            return codeElement;
        }

        CtInvocation<?> methodInvocation = (CtInvocation<?>) codeElement;
        List<CtExpression<?>> paramList = methodInvocation.getArguments();

        // 为每一个参数都生成一个局部变量声明表达式，插入到方法调用之前
        List<CtExpression<?>> replacedParams = new ArrayList<>();
        for (CtExpression param : paramList) {
            CtCodeSnippetStatement stat = methodInvocation.getFactory().createCodeSnippetStatement();
            CtVariable var = createLocalVariable(param);
            stat.setValue(var.toString());
            methodInvocation.getParent(CtStatement.class).insertBefore(stat);
            CtVariableRead varRead = methodInvocation.getFactory().createVariableRead();
            varRead.setVariable(var.getReference());
            replacedParams.add(varRead);
        }
        // 用临时变量替换原有方法调用中的参数
        methodInvocation.setArguments(replacedParams);
        return methodInvocation;
    }

    /**
     * 根据给定的方法调用实际参数表达式生成一个对应的局部变量生命表达式
     * @param paramExpr 方法调用中的参数表达式
     * @return 对应的局部变量声明语句
     */
    private CtLocalVariable createLocalVariable(CtExpression<?> paramExpr) {
        CtLocalVariable var = paramExpr.getFactory().createLocalVariable();
        int varTypeCount = varCount.getOrDefault(paramExpr.getType().getSimpleName(), 0);

        var.setSimpleName(generateVarName(paramExpr.getType().getSimpleName(), varTypeCount));
        var.setType(paramExpr.getType());
        var.setAssignment(paramExpr);

        varCount.put(paramExpr.getType().getSimpleName(), varTypeCount + 1);

        return var;
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

    public static void main(String[] args) {
        String originCode = "package test;\n" +
                "public class Demo {\n" +
                "    private static String name = \"demo\";\n" +

                "    public boolean testFunction() {\n" +
                "        String a = \"2\";\n" +
                "        boolean res = test2(a);\n" +
                "        boolean res = test2(getN());\n" +
                "        boolean res = test2(\"3\" + \"3\");\n" +
                "        System.out.println(test2(\"4\"));\n" +
                "    }\n" +
                "    private boolean test2(String a) {\n" +
                "        return a.equals(\"1\");\n" +
                "    }\n" +
                "\n" +
                "    private String getN() {\n" +
                "        return \"getN\";\n" +
                "    }\n" +
                "}";

        CtClass clazz = Launcher.parseClass(originCode);
        List<CtInvocation> invocations = clazz.filterChildren((Filter<CtInvocation>) element ->
                element.getExecutable().getSimpleName().equals("test2")).list();

        InvocationNormalizeAction action = new InvocationNormalizeAction();
        for (CtInvocation i : invocations) {
            CtElement e = action.transform(i, null);
//            System.out.println(e.getParent(CtMethod.class).prettyprint());
        }
    }
}
