package core.manipulation.bytecode.compiler.base;

import core.manipulation.bytecode.entity.CompilationResult;
import util.StringUtil;

import javax.tools.*;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaCompiler.CompilationTask;
import java.io.IOException;
import java.util.*;

/**
 * 用于实现java动态编译的编译器
 */
public class JavaXToolsCompiler {
    // 编译参数
    private List<String> options;
    // java编译器
    private JavaCompiler compiler;
    // 文件管理器
    private VirtualFileObjectManager fileManager;
    // 收集编译报错信息
    private DiagnosticCollector<JavaFileObject> diagnostics;

    public JavaXToolsCompiler() {
        this.options = Arrays.asList("-nowarn");
        this.compiler = ToolProvider.getSystemJavaCompiler();
        this.diagnostics = new DiagnosticCollector<>();
        StandardJavaFileManager standardJavaFileManager = this.compiler.getStandardFileManager(this.diagnostics, null, null);
        this.fileManager = new VirtualFileObjectManager(standardJavaFileManager);
    }

    /**
     * 编译代码，获得字节码和错误信息
     * @param qualifiedNameAndContent
     * @param compiledDependencies
     * @param options
     * @return
     */
    public synchronized CompilationResult javaBytecodeFor(Map<String, String> qualifiedNameAndContent, Map<String, byte[]> compiledDependencies, List<String> options) {
        this.diagnostics = new DiagnosticCollector<>();
        this.fileManager.getClassFiles().clear();

        Collection<JavaFileObject> units = addCompilationUnits(qualifiedNameAndContent);
        this.fileManager.addCompiledClasses(compiledDependencies);

        CompilationTask task = getCompiler().getTask(null, this.fileManager, getDiagnostics(), options, null, units);
        runCompilationTask(task);

        Map<String, byte[]> bytecodes = collectBytecodes(qualifiedNameAndContent);

        List<String> errors = new ArrayList<>();
        copyErrors(errors, this.diagnostics);

        CompilationResult compilationResult = new CompilationResult(bytecodes, errors);

        return compilationResult;
    }

    /**
     * 批量添加Compilation unit
     * @param qualifiedNameAndContent
     * @return
     */
    protected Collection<JavaFileObject> addCompilationUnits(Map<String, String> qualifiedNameAndContent) {
        Collection<JavaFileObject> units = new ArrayList<>();
        for (String qualifiedName : qualifiedNameAndContent.keySet()) {
            String sourceContent = qualifiedNameAndContent.get(qualifiedName);
            JavaFileObject sourceFile = addCompilationUnit(qualifiedName, sourceContent);
            units.add(sourceFile);
        }

        return units;
    }

    /**
     * 添加Compilation unit到文件管理器
     * @param qualifiedName
     * @param sourceContent
     * @return
     */
    protected JavaFileObject addCompilationUnit(String qualifiedName, String sourceContent) {
        String simpleClassName = StringUtil.lastAfterSplit(qualifiedName, '.');
        String packageName = StringUtil.stripEnd(qualifiedName, '.' + simpleClassName);
        // 这是原始的statement
        SourceCodeFileObject sourceFile = new SourceCodeFileObject(simpleClassName, sourceContent);
        this.fileManager.addSourceFile(StandardLocation.SOURCE_PATH, packageName, simpleClassName, sourceFile);

        return sourceFile;
    }

    /**
     * 从Diagnostic中复制编译错误信息到errors列表中
     * @param errors
     * @param diagnostics
     */
    private void copyErrors(List<String> errors, DiagnosticCollector<JavaFileObject> diagnostics) {
        for (Diagnostic d : diagnostics.getDiagnostics()) {
            if (d.getKind() == Kind.ERROR) {
                errors.add(d.toString());
            }
        }
    }

    /**
     * 进行编译（丢弃了编译错误信息）
     * @param task
     * @return
     */
    protected boolean runCompilationTask(CompilationTask task) {
        boolean success = task.call();
        if (!success) {
            Collection<String> errors = new ArrayList<>();
            for (Diagnostic<? extends JavaFileObject> diagnostic : this.diagnostics.getDiagnostics()) {
                errors.add(diagnostic.toString());
            }
        }

        return success;
    }

    /**
     * 获取类对应的字节码
     * @param qualifiedNameAndContent
     * @return
     */
    private Map<String, byte[]> collectBytecodes(Map<String, String> qualifiedNameAndContent) {
        Map<String, byte[]> bytecodes = new HashMap<>();
        Map<String, CompiledOutputFileObject> classFiles = this.fileManager.getClassFiles();

        for (String qualifiedName : classFiles.keySet()) {
            String topClassName = topClassName(qualifiedName);
            if (qualifiedNameAndContent.containsKey(topClassName)) {
                bytecodes.put(qualifiedName, classFiles.get(qualifiedName).byteCodes());
            }
        }

        return bytecodes;
    }

    /**
     * 获取顶级类名
     * 局部内部类的类定义在方法里,命名的规律为：外部类名$内部类名.class
     * @param qualifiedName
     * @return
     */
    private String topClassName(String qualifiedName) {
        return qualifiedName.split("[$]")[0];
    }

    private List<String> getOptions() {
        return options;
    }

    private JavaCompiler getCompiler() {
        return compiler;
    }

    private DiagnosticCollector<JavaFileObject> getDiagnostics() {
        return diagnostics;
    }

    public static void main(String[] args) throws IOException {
        JavaXToolsCompiler compiler = new JavaXToolsCompiler();

        Map<String, String> files = new HashMap<>();
        Map<String, byte[]> dep = new HashMap<>();
        files.put("Main", "public class Main {private int a = 1; private void test() {shadow.util.MyMathUtil.sum2(1,2,3);}}");

        List<String> cps = new ArrayList<>();
        cps.add("-cp");
//        String path = "E:\\projects\\libraryupdater\\examples\\migration_test_demo\\lib\\migrate_util_shadow-2.0.jar";
        String path = "/E:/projects/libraryupdater/examples/migration_test_demo/lib/migrate_util_shadow-2.0.jar";
        cps.add(path);
        CompilationResult res = compiler.javaBytecodeFor(files, dep, cps);

        System.out.println(res);
    }
}
