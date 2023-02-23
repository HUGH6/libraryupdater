package core.template.diff.entity.action;

import core.migration.util.MigrationSupporter;
import core.template.diff.ApiElementBuilder;
import core.template.diff.entity.ActionContext;
import core.template.diff.entity.ApiElement;
import core.template.diff.entity.Diff;
import core.template.diff.entity.TransferAction;
import core.template.diff.entity.diff.AddExceptionDiff;
import org.apache.log4j.Logger;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Filter;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class AddExceptionAction implements TransferAction {
    private static final Logger logger = Logger.getLogger(AddExceptionAction.class.getName());
    // 差异信息
    private AddExceptionDiff diff = null;

    public AddExceptionAction(Diff diff) {
        if (diff instanceof AddExceptionDiff) {
            this.diff = (AddExceptionDiff) diff;
        }
    }
    /**
     * 在代码元素上应用转换
     * @param codeElement
     * @return 转换后的代码元素
     */
    @Override
    public CtElement transform(CtElement codeElement, ActionContext context) {
        if (this.diff == null) {
            logger.warn("Invalid add exception action, diff info is invalid");
            return codeElement;
        }

        if (!(codeElement instanceof CtInvocation)) {
            logger.warn("Invalid add exception action, code element is not CtInvocation");
            return codeElement;
        }

        if (this.diff.exceptionToAdd == null || "".equals(this.diff.exceptionToAdd)) {
            logger.warn("Invalid add exception action, diff info is invalid");
            return codeElement;
        }

        // 方法调用
        CtInvocation<?> transformedElement = (CtInvocation<?>) codeElement;
        // 方法调用所在的语句，将再该语句外部添加try catch语句
        CtStatement currentStatement = transformedElement.getParent(CtStatement.class);
        currentStatement.getElements(new Filter<CtInvocation>() {
            @Override
            public boolean matches(CtInvocation invocation) {
                invocation.replace(transformedElement);
                return true;
            }
        });

        // 尝试获取是否有try语句
        CtTry ct = transformedElement.getParent(CtTry.class);
        if (ct == null) {
            // 若没有try语句，则新建一个try语句，将当前调用的语句放入try语句内部，然后用try语句替换原本的调用语句
            ct = MigrationSupporter.getFactory().Core().createTry();
            // 将原本的statement写入try 块中
            ct.setBody(currentStatement.clone());
            // 新建一个block用于存放try块
            CtBlock tryParentBlock = MigrationSupporter.getFactory().createBlock();
            tryParentBlock.addStatement(ct);
            // 将原本的statement替换为新建的try block
            currentStatement.replace(tryParentBlock);
        }

        // 为try语句添加新的catch语句块
        CtCatch catcher = MigrationSupporter.getFactory().createCatch();
        // 异常名称
        String fullQualifiedNameException = this.diff.exceptionToAdd;
        CtTypeReference reference = MigrationSupporter.getFactory().Type().createReference(fullQualifiedNameException);
        // 创建一个变量，作为catch块的参数
        CtCatchVariable<? extends Throwable> catchVariable = MigrationSupporter.getFactory().createCatchVariable(reference, fullQualifiedNameException);
        catchVariable.setSimpleName("e");
        // 为catch块设置参数
        catcher.setParameter(catchVariable);
        // 为catch块添加一个空block
        catcher.setBody(MigrationSupporter.getFactory().createBlock());
        ct.addCatcher(catcher);

        // 检查是否需要import该异常
        importExceptionInNeed(fullQualifiedNameException, transformedElement);

        return transformedElement;
    }

    private void importExceptionInNeed(String qualifiedNameException, CtElement element) {
        boolean imported = false;
        List<CtImport> imports = element.getPosition().getCompilationUnit().getImports();
        for (CtImport i : imports) {
            Set<CtTypeReference<?>> set = i.getReferencedTypes();
            Iterator it = set.iterator();
            while (it.hasNext()) {
                CtTypeReference<?> r = (CtTypeReference<?>) it.next();
                String name = r.getQualifiedName();

                if (name.equals(qualifiedNameException)) {
                    imported = true;
                }
            }
        }

        // 若未import，则添加import语句
        if (!imported) {
            CtTypeReference reference = MigrationSupporter.getFactory().Type().createReference(qualifiedNameException);
            CtImport newImportStat = MigrationSupporter.getFactory().createImport(reference);
            imports.add(newImportStat);
            element.getPosition().getCompilationUnit().setImports(imports);
            MigrationSupporter.getEnvironment().setAutoImports(true);
        }
    }

    public static void main(String[] args) {
        SpoonAPI spoon = new Launcher();
        spoon.addInputResource("E:\\projects\\migration_test_demo\\src\\main\\java");
        spoon.buildModel();

        String targetMethodName = "util.AddExceptionUtil.function1";

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

        ApiElement originApi = ApiElementBuilder.buildApiElement("int util.AddExceptionUtil.function1()");
        ApiElement targetApi = ApiElementBuilder.buildApiElement("int util.AddExceptionUtil.function1() throws java.lang.IOException");

        AddExceptionDiff diff = new AddExceptionDiff(originApi, targetApi, "java.lang.IOException");
        AddExceptionAction action = new AddExceptionAction(diff);
        ActionContext context = new ActionContext(originApi, targetApi);

        for (CtInvocation i : invocations) {
            CtElement e = action.transform(i, context);
            CtElement f = e.getParent(CtMethod.class);
            System.out.println(f.prettyprint());
        }
    }

    @Override
    public String toString() {
        return "Diff: Add Exception " + this.diff.exceptionToAdd;
    }
}
