package core.manipulation.bytecode;

import conf.ConfigurationProperties;
import core.manipulation.bytecode.compiler.SpoonClassCompiler;
import core.manipulation.bytecode.entity.CompilationResult;
import core.setup.PropertyKey;
import org.apache.log4j.Logger;
import spoon.compiler.Environment;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.support.JavaOutputProcessor;
import util.ClassFileUtil;

import java.io.File;

/**
 * 该类用于保存Java代码到磁盘
 */
public class OutputWritter {
    private JavaOutputProcessor javaPrinter;
    private Factory factory;
    public static final String CLASS_EXT = ".class";
    private Logger logger = Logger.getLogger(SpoonClassCompiler.class.getName());

    public OutputWritter(Factory factory) {
        super();
        this.factory = factory;
    }

    /**
     * 更新输出器
     * @param output
     */
    public void updateOutput(String output) {
        getEnvironment().setSourceOutputDirectory(new File(output));
        JavaOutputProcessor fileOutput = new JavaOutputProcessor(new DefaultJavaPrettyPrinter(getEnvironment()));
        fileOutput.setFactory(getFactory());

        this.javaPrinter = fileOutput;
    }

    /**
     * 保存CtClass对于的代码对象的源码到磁盘
     * @param element
     */
    public void saveSourceCode(CtClass element) {
        this.getEnvironment().setCommentEnabled(true);
        this.getEnvironment().setPreserveLineNumbers(ConfigurationProperties.getPropertyBool(PropertyKey.PreserveLineNumber));
        if (javaPrinter == null) {
            throw new IllegalArgumentException("Java printer is null");
        }
        if (!element.isTopLevel()) {
            return;
        }
        // Create Java code and create ICompilationUnit
        try {
            javaPrinter.getCreatedFiles().clear();
            javaPrinter.process(element);
        } catch (Exception e) {
            logger.error("Error saving ctclass " + element.getQualifiedName());
        }
    }

    /**
     * 保存编译的二进制代码到磁盘
     * @param compilation
     * @param outputDir
     */
    public void saveByteCode(CompilationResult compilation, File outputDir) {
        try {
            outputDir.mkdirs();

            for (String compiledClassName : compilation.getByteCodes().keySet()) {
                String fileName = new String(compiledClassName).replace('.', File.separatorChar) + CLASS_EXT;
                byte[] compiledClass = compilation.getByteCodes().get(compiledClassName);
                ClassFileUtil.writeToDisk(true, outputDir.getAbsolutePath(), fileName, compiledClass);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Environment getEnvironment() {
        return this.getFactory().getEnvironment();
    }

    public Factory getFactory() {
        return this.factory;
    }

    public JavaOutputProcessor getJavaPrinter() {
        return javaPrinter;
    }

    public void setJavaPrinter(JavaOutputProcessor javaPrinter) {
        this.javaPrinter = javaPrinter;
    }
}
