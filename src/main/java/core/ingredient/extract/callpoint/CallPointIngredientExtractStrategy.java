package core.ingredient.extract.callpoint;

import core.ingredient.Ingredient;
import core.ingredient.IngredientExtractStrategy;
import core.ingredient.IngredientPool;
import core.migration.util.MigrationSupporter;
import core.template.diff.ApiElementBuilder;
import core.template.diff.entity.ApiElement;
import core.template.diff.entity.ParamElement;
import org.apache.log4j.Logger;
import spoon.Launcher;
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.Filter;

import java.util.*;


/**
 * 该类是用于提取API调用点附件上下文中局部变量、字面量、表达式、方法参数、类成员字段的提取策略实现
 */
public class CallPointIngredientExtractStrategy implements IngredientExtractStrategy {
     private static Logger logger = Logger.getLogger(CallPointIngredientExtractStrategy.class.getName());

    /**
     * 返回相关代码元素中所有的变量
     * CtVariableAccess表示对一个变量的访问（读或写）
     * @param codeElement 用于变量提取的代码元素
     * @return 提取的变量列表
     */
    public static List<CtVariableAccess<?>> extractVariableAccess(CtElement codeElement) {
        List<CtVariableAccess<?>> varAccess = new ArrayList<>();
        List<String>            varNames = new ArrayList<>();

        CtScanner scanner = new CtScanner() {
            @Override
            public <T> void visitCtVariableRead(CtVariableRead<T> variableRead) {
                super.visitCtVariableRead(variableRead);
                add(variableRead);
            }

            @Override
            public <T> void visitCtVariableWrite(CtVariableWrite<T> variableWrite) {
                super.visitCtVariableWrite(variableWrite);
                add(variableWrite);
            }

            @Override
            public <T> void visitCtFieldRead(CtFieldRead<T> fieldRead) {
                super.visitCtFieldRead(fieldRead);
                add(fieldRead);
            }

            @Override
            public <T> void visitCtFieldWrite(CtFieldWrite<T> fieldWrite) {
                super.visitCtFieldWrite(fieldWrite);
                add(fieldWrite);
            }

            /**
             * 将VariableAccess和变量名添加到结果列表中
             * @param varAcc 待添加的VariableAccess对象
             */
            private void add(CtVariableAccess<?> varAcc) {
                String varName = varAcc.getVariable().toString();
                String varType = varAcc.getVariable().getType().toString();
                if (!varNames.contains(varName)) {
                    System.out.println(varName);
                    System.out.println(varType);
                    System.out.println();

                    varAccess.add(varAcc);
                    varNames.add(varAcc.getVariable().getSimpleName());
                }
            }
        };

        scanner.scan(codeElement);

        return varAccess;
    }

    public static Map<String, List<Ingredient>> extractFiled(CtElement codeElement) {
        Map<String, List<Ingredient>> ingredients = new HashMap<>();

        CtClass<?> methodDefine = null;
        if (!(codeElement instanceof CtClass)) {
            methodDefine = codeElement.getParent(CtClass.class);
        } else {
            methodDefine = (CtClass<?>) codeElement;
        }

        if (methodDefine == null) {
            return ingredients;
        }

        List<CtField<?>> fields = new ArrayList<>();
        List<String>     fieldNames = new ArrayList<>();

        CtScanner scanner = new CtScanner() {
            @Override
            public <T> void visitCtField(CtField<T> field) {
                super.visitCtField(field);
                add(field);
            }

            private void add(CtField<?> field) {
                String varName = field.getSimpleName();
                String varType = field.getType().getQualifiedName();

                if (!fieldNames.contains(varName)) {
                    fields.add(field);
                    fieldNames.add(varName);
                }
            }
        };

        scanner.scan(methodDefine);

        if (!fieldNames.isEmpty()) {
            for (int idx = 0; idx < fieldNames.size(); idx++) {
                CtField f = fields.get(idx);
                String varName = fieldNames.get(idx);
                String varType = f.getType().getQualifiedName();

                List<Ingredient> ingredientList = ingredients.getOrDefault(varType, new ArrayList<>());
                CtVariableRead variableRead = codeElement.getFactory().createVariableRead();
                variableRead.setVariable(f.getReference());
                ingredientList.add(new Ingredient(variableRead, varName));
                ingredients.put(varType, ingredientList);
            }
        }

        return ingredients;
    }

    public static Map<String, List<Ingredient>> extractLocalVariable(CtElement codeElement) {
        Map<String, List<Ingredient>> ingredients = new HashMap<>();

        CtMethod<?> methodDefine = null;
        if (!(codeElement instanceof CtMethod)) {
            methodDefine = codeElement.getParent(CtMethod.class);
        } else {
            methodDefine = (CtMethod<?>) codeElement;
        }

        if (methodDefine == null) {
            return ingredients;
        }

        List<CtLocalVariable<?>> variables = new ArrayList<>();
        List<String>            varNames = new ArrayList<>();

        CtScanner scanner = new CtScanner() {
            @Override
            public <T> void visitCtLocalVariable(CtLocalVariable<T> localVariable) {
                super.visitCtLocalVariable(localVariable);
                add(localVariable);
            }

            private void add(CtLocalVariable<?> localVariable) {
                String varName = localVariable.getSimpleName();
                String varType = localVariable.getType().getQualifiedName();

                if (!varNames.contains(varName)) {
                    variables.add(localVariable);
                    varNames.add(varName);
                }
            }
        };

        scanner.scan(methodDefine);

        if (!varNames.isEmpty()) {
            for (int idx = 0; idx < varNames.size(); idx++) {
                CtLocalVariable va = variables.get(idx);
                String varName = varNames.get(idx);
                String varType = va.getType().getQualifiedName();

                List<Ingredient> ingredientList = ingredients.getOrDefault(varType, new ArrayList<>());
                CtVariableRead variableRead = codeElement.getFactory().createVariableRead();
                variableRead.setVariable(va.getReference());
                ingredientList.add(new Ingredient(variableRead, varName));
                ingredients.put(varType, ingredientList);
            }
        }

        return ingredients;
    }

    /**
     * 提取代码元素中的字面量信息
     * @param codeElement 代码元素
     * @return 字面量信息
     */
    public static List<CtLiteral<?>> extractLiteral(CtElement codeElement) {
        List<CtLiteral<?>> literalValues = new ArrayList<>();

        CtScanner scanner = new CtScanner() {
            @Override
            public <T> void visitCtLiteral(CtLiteral<T> literal) {
                super.visitCtLiteral(literal);

                if (!literalValues.contains(literal)) {
                    System.out.println(literal.getType().getQualifiedName());
                    System.out.println(literal.getValue());
                    literalValues.add(literal);
                }
            }
        };

        scanner.scan(codeElement);
        return literalValues;
    }

    /**
     * 提取代码元素所在的方法定义中的方法参数信息
     * @param codeElement 代码元素
     * @return 参数信息
     */
    public static Map<String, List<Ingredient>> extractMethodParameter(CtElement codeElement) {
        Map<String, List<Ingredient>> ingredients = new HashMap<>();

        CtMethod<?> methodDefine = null;
        if (!(codeElement instanceof CtMethod)) {
            methodDefine = codeElement.getParent(CtMethod.class);
        } else {
            methodDefine = (CtMethod<?>) codeElement;
        }

        if (methodDefine == null) {
            return ingredients;
        }

        List<CtParameter<?>> parameters = methodDefine.getParameters();
        for (CtParameter<?> p : parameters) {
            String paramName = p.getSimpleName();
            String paramType = p.getType().getQualifiedName();

            List<Ingredient> ingredientList = ingredients.getOrDefault(paramType, new ArrayList<>());
            CtVariableRead paramRead = codeElement.getFactory().createVariableRead();
            paramRead.setVariable(p.getReference());
            ingredientList.add(new Ingredient(paramRead, paramName));
            ingredients.put(paramType, ingredientList);
        }

        return ingredients;
    }

    /**
     * 提取方法调用点自身的方法实参
     * @param invocation
     * @param apiInfo
     * @return
     */
    public static Map<String, List<Ingredient>> extractInvocationArgument(CtInvocation invocation, ApiElement apiInfo) {
        Map<String, List<Ingredient>> ingredients = new HashMap<>();

        List<CtExpression<?>> arguments = invocation.getArguments();

        for (int idx = 0; idx < apiInfo.params.size(); idx++) {
            ParamElement paramInfo = apiInfo.params.get(idx);
            String paramName = paramInfo.getName();
            String paramType = paramInfo.getQualifiedType();

            CtExpression<?> expression = arguments.get(idx);

            List<Ingredient> ingredientList = ingredients.getOrDefault(paramType, new ArrayList<>());
            ingredientList.add(new Ingredient(expression, paramName));
            ingredients.put(paramType, ingredientList);
        }

        return ingredients;
    }

    public static Map<String, List<Ingredient>> getDefaultValue(ApiElement api) {
        Map<String, List<Ingredient>> ingredients = new HashMap<>();
        Set<String> paramTypes = new HashSet<>();
        for (ParamElement p : api.params) {
            paramTypes.add(p.getQualifiedType());
        }

        if (!paramTypes.isEmpty()) {
            for (String type : paramTypes) {
                List<Ingredient> ingredientList = ingredients.getOrDefault(type, new ArrayList<>());
                ingredientList.addAll(getDefaultValueByType(type));
                ingredients.put(type, ingredientList);
            }
        }

        return ingredients;
    }

    public static List<Ingredient> getDefaultValueByType(String type) {
        List<Ingredient> res = new ArrayList<>();
        if (type.equals("int") || type.equals("java.lang.Integer")) {
            res.add(new Ingredient(MigrationSupporter.getFactory().createLiteral().setValue(0)));
            res.add(new Ingredient(MigrationSupporter.getFactory().createLiteral().setValue(1)));
        } else if (type.equals("double") || type.equals("java.lang.Double")) {
            res.add(new Ingredient(MigrationSupporter.getFactory().createLiteral().setValue(0.0)));
        } else if (type.equals("float") || type.equals("java.lang.Float")) {
            res.add(new Ingredient(MigrationSupporter.getFactory().createLiteral().setValue(0.0f)));
        } else if (type.equals("boolean") || type.equals("java.lang.Boolean")) {
            res.add(new Ingredient(MigrationSupporter.getFactory().createLiteral().setValue(true)));
            res.add(new Ingredient(MigrationSupporter.getFactory().createLiteral().setValue(false)));
        } else if (type.equals("java.lang.String")) {
            res.add(new Ingredient(MigrationSupporter.getFactory().createLiteral().setValue("")));
        } else {
            res.add(new Ingredient(MigrationSupporter.getFactory().createLiteral().setValue(null)));
        }

        return res;
    }

    /**
     * 提取方法调用点附近的上下文
     * @param invocation
     * @param originApi
     * @return
     */
    public static IngredientPool extractIngredient(CtInvocation invocation, ApiElement originApi) {
        IngredientPool ingredientPool = new IngredientPool();
        // 方法调用自身的参数
        Map<String, List<Ingredient>> ingredients1 = extractInvocationArgument(invocation, originApi);
        ingredientPool.addAll(ingredients1);
        // 方法调用所在外部方法的局部变量
        Map<String, List<Ingredient>> ingredients2 = extractLocalVariable(invocation);
        ingredientPool.addAll(ingredients2);
        // 方法调用所在外部方法的参数
        Map<String, List<Ingredient>> ingredients3 = extractMethodParameter(invocation);
        ingredientPool.addAll(ingredients3);
        // 方法调用所在类的成员字段
        Map<String, List<Ingredient>> ingredients4 = extractFiled(invocation);
        ingredientPool.addAll(ingredients4);

        Map<String, List<Ingredient>> ingredients5 = getDefaultValue(originApi);
        ingredientPool.addAll(ingredients5);
        return ingredientPool;
    }

    public static void main(String[] args) {
        String originCode = "package test;\n" +
                "public class Demo {\n" +
                "    private static String name = \"demo\";\n" +

                "    public boolean testFunction(int good) {\n" +
                "        String a = \"2\";\n" +
                "        boolean res = test2(a);\n" +
                "        //boolean res = test2(getN());\n" +
                "        //boolean res = test2(\"3\" + \"3\");\n" +
                "        //System.out.println(test2(\"4\"));\n" +
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

        ApiElement api = ApiElementBuilder.buildApiElement("boolean test.Demo.test2(java.lang.String a)");
        //Map<String, List<Ingredient>> ingredients = extractInvocationArgument(invocations.get(0), api);
//        Map<String, List<Ingredient>> ingredients = extractMethodParameter(invocations.get(0));
//        Map<String, List<Ingredient>> ingredients = extractLocalVariable(invocations.get(0));
//        Map<String, List<Ingredient>> ingredients = extractFiled(invocations.get(0));

        IngredientPool pool = extractIngredient(invocations.get(0), api);

        System.out.println(pool);

    }
}
