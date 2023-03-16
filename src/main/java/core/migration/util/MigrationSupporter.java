package core.migration.util;

import conf.ConfigurationProperties;
import core.entity.OperatorInstance;
import core.entity.ProgramVariant;
import core.manipulation.bytecode.OutputWritter;
import core.setup.ProjectMigrationFacade;
import spoon.compiler.Environment;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 用于支持迁移过程中的语法树操作
 */
public class MigrationSupporter {
    // 单例实例
    public static MigrationSupporter instance = null;

    private Factory factory;
    // 用于保存代码和字节码到磁盘
    private OutputWritter outputer;

    /**
     * 将其实现为单例模式，由于没有并发情况，写成简单单例
     * @return
     */
    public static MigrationSupporter getSupporter() {
        if (instance == null) {
            instance = new MigrationSupporter();
        }
        return instance;
    }

    private MigrationSupporter() {
        this(getFactory(), getFactory().getEnvironment());
    }

    private MigrationSupporter(Factory factory, Environment environment) {
        this.factory = factory;
        this.outputer = new OutputWritter(factory);
    }

    public void buildModel(String srcPathToBuild, String[] classpath) {
        buildModel(srcPathToBuild, null, classpath);
    }

    public void buildModel(String srcPathToBuild, String bytecodePathToBuild, String[] classpath) {
        JDTBasedSpoonCompiler jdtBasedSpoonCompiler = null;
        factory.getEnvironment().setCommentEnabled(true);
        factory.getEnvironment().setPreserveLineNumbers(true);
        factory.getEnvironment().setIgnoreDuplicateDeclarations(false);

        jdtBasedSpoonCompiler = new JDTBasedSpoonCompiler(factory);

        // 添加源文件
        String[] sources = srcPathToBuild.split(File.pathSeparator);
        for (String src : sources) {
            if (!src.trim().isEmpty()) {
                jdtBasedSpoonCompiler.addInputSource(new File(src));
            }
        }

        if (classpath != null && classpath.length > 0) {
            jdtBasedSpoonCompiler.setSourceClasspath(classpath);
        }
        jdtBasedSpoonCompiler.build();
    }

    /**
     * 对目标待迁移项目构建model
     * @param facade
     * @throws Exception
     */
    public void buildSpoonModel(ProjectMigrationFacade facade) throws Exception {
        String codeLocation = "";
        List<String> codeLocations = facade.getProjectConfiguration().getOriginalDirSrc();
        for (String source : codeLocations) {
            codeLocation += source + File.pathSeparator;
        }
        if (codeLocation.length() > 0) {
            codeLocation = codeLocation.substring(0, codeLocation.length() - 1);
        }

        String bytecodeLocation = facade.getMutatorOutDirWithPrefix(ProgramVariant.DEFAULT_ORIGINAL_VARIANT);
        String classpath = facade.getProjectConfiguration().getDependenciesString();
        classpath += File.pathSeparator + new File(ConfigurationProperties.getProperty(ConfigurationProperties.TARGET_LIBRARY_PATH)).getPath();
        String[] cpArray = (classpath != null && !classpath.trim().isEmpty()) ? classpath.split(File.pathSeparator) : null;

        try {
            this.buildModel(codeLocation, bytecodeLocation, cpArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 克隆一个代码元素
     * @param element
     * @return
     */
    public static CtCodeElement clone(CtCodeElement element) {
        CtCodeElement clonedElement = MigrationSupporter.getSupporter().factory.Core().clone(element);
        clonedElement.setParent(element.getParent());
        return clonedElement;
    }

    /**
     * 保存 Java 文件并对其进行编译 程序变体以及项目的其余部分保存在磁盘上。不再：此外，编译后的类将其保存在磁盘上。最后，当前线程引用了具有 ProgramVariant 的类加载器
     * @param variant
     * @param srcOutputPath
     * @throws Exception
     */
    public void saveProgramVariantSourceCodeOnDisk(ProgramVariant variant, String srcOutputPath) throws Exception {
        // 设置输出目录
        this.outputer.updateOutput(srcOutputPath);
        Collection<CtClass> classes = new ArrayList<>();

        // 只保存被操作影响的类
        List<OperatorInstance> ops = variant.getAllOperations();
        for (OperatorInstance op : ops) {
            CtClass classOfOp = op.getModificationPoint().getCtClass();
            if (classOfOp != null && !classes.contains(classOfOp)) {
                classes.add(classOfOp);
            }
        }

        if (classes.isEmpty()) {
            classes = variant.getBuiltClasses().values();
        }

        for (CtClass clazz : classes) {
            generateSourceFromCtClass(clazz);
        }
    }

    /**
     * 将类的源代码保存到源文件中，调用前需要先调用updateOutput(srcOutput)设置输出路径
     * @param type
     */
    public void generateSourceFromCtClass(CtType<?> type) {
        SourcePosition sp = type.getPosition();
        type.setPosition(null);

        if (this.outputer == null || this.outputer.getJavaPrinter() == null) {
            throw new IllegalStateException("spoon compiler must be initialized");
        }

        this.outputer.saveSourceCode((CtClass) type);
        type.setPosition(sp);
    }

    public static Factory getFactory() {
        if (instance == null) {
            Factory factory = createFactory();
            factory.getEnvironment().setLevel("OFF");
            return factory;
        }
        return instance.factory;
    }

    private static Factory createFactory() {
        Environment env = getEnvironment();
        Factory factory = new FactoryImpl(new DefaultCoreFactory(), env);
        return factory;
    }

    public static Environment getEnvironment() {
        StandardEnvironment env = new StandardEnvironment();
        Integer complianceLevel = 8;
        env.setComplianceLevel(complianceLevel);
        env.setVerbose(false);
        env.setDebug(true);
        env.setTabulationSize(5);
        env.useTabulations(true);
        return env;
    }

    public OutputWritter getOutputer() {
        return this.outputer;
    }

    public void setOutputer(OutputWritter outputer) {
        this.outputer = outputer;
    }
}
