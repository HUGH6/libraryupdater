package core.manipulation.bytecode.compiler;

import conf.ConfigurationProperties;
import core.entity.ProgramVariant;
import core.manipulation.bytecode.compiler.base.JavaXToolsCompiler;
import core.manipulation.bytecode.entity.CompilationResult;
import core.migration.util.MigrationSupporter;
import spoon.processing.ProcessingManager;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.support.RuntimeProcessingManager;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * 编译一个Spoon class
 * 将编译结果保存在内存中
 * 这个加载器的特点是会创建给定CtClass的字节码
 */
public class SpoonClassCompiler implements VariantCompiler {
    private Factory factory;

    private ProcessingManager processing;

    private DefaultJavaPrettyPrinter prettyPrinter;

    private JavaXToolsCompiler dcc = new JavaXToolsCompiler();

    public SpoonClassCompiler() {
        this.factory = MigrationSupporter.getFactory();
    }

    public SpoonClassCompiler(Factory factory) {
        this.factory = factory;
    }

    /**
     * 通过程序变体进行编译
     *
     * @param instanceToCompile
     * @param classpath
     * @return
     */
    @Override
    public CompilationResult compile(ProgramVariant instanceToCompile, URL[] classpath) {
        List<CtType<?>> ctClasses = instanceToCompile.getClassesAffectedByOperators();

        if (ctClasses == null || ctClasses.isEmpty()) {
            // 如果没有被影响的类，则取所有类
            ctClasses = instanceToCompile.getAllClasses();

            if (ctClasses == null || ctClasses.isEmpty()) {
                throw new IllegalStateException("No class to compile");
            }
        }

        // ctClasses = instanceToCompile.getAllClasses();

        CompilationResult result = this.compile(ctClasses, classpath);

        return result;
    }

    /**
     * 通过类模型进行编译
     *
     * @param classesToCompile
     * @param classpath
     * @return
     */
    @Override
    public CompilationResult compile(Collection<? extends CtType> classesToCompile, URL[] classpath) {
        Map<String, String> toCompile = new HashMap<>();
        this.prettyPrinter = new DefaultJavaPrettyPrinter(this.getFactory().getEnvironment());

        for (CtType ctClass : classesToCompile) {
            try {
                this.getProcessingManager().process(ctClass);
                toCompile.put(ctClass.getQualifiedName(), sourceForModelledClass(ctClass));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            return compile(classpath, toCompile);
        } catch (Exception e) {
            e.printStackTrace();
            throw  e;
        }
    }

    /**
     * 编译代码
     * @param classpath
     * @param toCompile
     * @return
     */
    public CompilationResult compile(URL[] classpath, Map<String, String> toCompile) {
        // -cp
        List<String> cps = new ArrayList<>();
        cps.add("-cp");
        String path = "";
        for (URL url : classpath) {
            try {
                path += ((new File(url.toURI()).getPath()) + File.pathSeparator);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        cps.add(path);

        // -cource
        String compliance = ConfigurationProperties.getProperty(ConfigurationProperties.COMPILATION_LEVEL);
        cps.add("-source");
        cps.add("1."+compliance);

        // -target
        cps.add("-target");
        cps.add("1."+compliance);

        cps.add("-encoding");
        cps.add("UTF-8");

        this.dcc = new JavaXToolsCompiler();
        CompilationResult result = dcc.javaBytecodeFor(toCompile, new HashMap<String, byte[]>(), cps);
        return result;
    }

    /**
     * 通过pretty printer打印出的代码，从spoon模型中提取源代码
     * @param modelledClass
     * @return
     */
    protected synchronized String sourceForModelledClass(CtType<?> modelledClass) {
        this.getFactory().getEnvironment().setAutoImports(true);
        this.prettyPrinter = new DefaultJavaPrettyPrinter(this.getFactory().getEnvironment());
        this.prettyPrinter.scan(modelledClass);

        String sourceCode = "";
        if (!modelledClass.getPackage().toString().equals("unnamed package")) {
            sourceCode = "package " + modelledClass.getPackage().toString() + ";"
                    + System.getProperty("line.separator") + this.prettyPrinter.toString();
        } else {
            sourceCode = System.getProperty("line.separator") + this.prettyPrinter.toString();
        }


        this.prettyPrinter = new DefaultJavaPrettyPrinter(this.getFactory().getEnvironment());

        return sourceCode;
    }

    public Factory getFactory() {
        return this.factory;
    }

    public ProcessingManager getProcessingManager() {
        if (this.processing == null) {
            this.processing = new RuntimeProcessingManager(this.getFactory());
        }
        return this.processing;
    }
}
