package core.template.diff.entity.action;

import core.migration.util.MigrationSupporter;
import core.template.diff.ApiElementBuilder;
import core.template.diff.entity.ActionContext;
import core.template.diff.entity.ApiElement;
import core.template.diff.entity.Diff;
import core.template.diff.entity.TransferAction;
import core.template.diff.entity.diff.NameChangeDiff;
import org.apache.log4j.Logger;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Filter;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class NameChangeAction implements TransferAction {
    private static final Logger logger = Logger.getLogger(NameChangeAction.class.getSimpleName());
    // 差异信息
    private NameChangeDiff diff = null;

    public NameChangeAction(Diff diff) {
        if (diff instanceof NameChangeDiff) {
            this.diff = (NameChangeDiff) diff;
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
            logger.warn("Invalid name change action, diff info is invalid");
            return codeElement;
        }

        if (!(codeElement instanceof CtInvocation)) {
            logger.warn("Invalid name change action, code element is not CtInvocation");
            return codeElement;
        }

        String originName = this.diff.originName;
        String targetName = this.diff.targetName;
        if ((originName == null || originName.length() == 0) || (targetName == null || targetName.length() == 0)) {
            logger.warn("Invalid name change action, origin name or target name is empty");
            return codeElement;
        }


        CtInvocation<?> transformedElement = (CtInvocation<?>) codeElement;

        String classOfMethod = transformedElement.getExecutable().getDeclaringType().getQualifiedName();
        String methodSignature = transformedElement.getExecutable().getSignature();
        String methodName = methodSignature.substring(0, methodSignature.indexOf('('));
        String fullMethodName = classOfMethod + "." + methodName;

        if (!fullMethodName.equals(originName)) {
            // 不是目标方法调用，不做转换
            return codeElement;
        }



        // 先查找一下新类是否已经import
        List<CtImport> imports = transformedElement.getPosition().getCompilationUnit().getImports();

        int lastDotIdx = targetName.lastIndexOf('.');
        String targetClass = targetName.substring(0, lastDotIdx);
        String targetMethodName = targetName.substring(lastDotIdx + 1);

        boolean imported = false;
        for (CtImport i : imports) {
            Set<CtTypeReference<?>> set = i.getReferencedTypes();
            Iterator it = set.iterator();
            while (it.hasNext()) {
                CtTypeReference<?> r = (CtTypeReference<?>) it.next();
                String name = r.getQualifiedName();

                if (name.equals(targetClass)) {
                    imported = true;
                }
            }
        }

        // 若未import，则添加import语句
        if (!imported) {
            CtTypeReference reference = MigrationSupporter.getFactory().Type().createReference(targetClass);
            CtImport newImportStat = MigrationSupporter.getFactory().createImport(reference);
            imports.add(newImportStat);
            transformedElement.getPosition().getCompilationUnit().setImports(imports);
            MigrationSupporter.getEnvironment().setAutoImports(true);
        }

        String originTargetName = transformedElement.getTarget().toString();
        if (!targetClass.equals(originTargetName)) {
            transformedElement.setTarget(MigrationSupporter.getFactory().createCodeSnippetExpression(targetClass));
        }

        transformedElement.getExecutable().setSimpleName(targetMethodName);
        return transformedElement;
    }

    public static void main(String[] args) {
        SpoonAPI spoon = new Launcher();
        spoon.addInputResource("E:\\projects\\migration_test_demo\\src\\main\\java");
        spoon.buildModel();

        String targetMethodName = "util.NameChangeUtil.test";

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

        ApiElement originApi = ApiElementBuilder.buildApiElement("void util.NameChangeUtil.test()");
        ApiElement targetApi = ApiElementBuilder.buildApiElement("void util.NameChangeUtil2.test()");

        NameChangeDiff diff = new NameChangeDiff(originApi, targetApi,originApi.name, targetApi.name);
        NameChangeAction action = new NameChangeAction(diff);
        ActionContext context = new ActionContext(originApi, targetApi);
        for (CtInvocation i : invocations) {
            CtElement e = action.transform(i, context);
            System.out.println(e.getPosition().getCompilationUnit().prettyprint());
        }
    }

    @Override
    public String toString() {
        return "Diff: Change name from " + this.diff.originName + " to " + this.diff.targetName;
    }
}