package util;

import conf.ConfigurationProperties;
import core.manipulation.bytecode.OutputWritter;
import core.setup.PropertyKey;
import spoon.compiler.Environment;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;

import java.io.File;
import java.util.List;

/**
 * 辅助类，用于构建项目ast模型
 */
public class UpdateSupporter {
    public static UpdateSupporter currentSupporter = null;
    public static Factory factory;
    // 用于保存代码和字节码到磁盘
    private OutputWritter output;

    public UpdateSupporter() {
        this(getFactory(), getFactory().getEnvironment());
    }

    public UpdateSupporter(Factory factory, Environment environment) {
        this.factory = factory;
        this.currentSupporter = this;
        this.output = new OutputWritter(factory);
    }
    public void buildSpoonModel(ProjectFacade projectFacade) throws Exception {
        String codeLocation = "";
        if (ConfigurationProperties.getPropertyBool(PropertyKey.ParseSourceFromOriginal)) {
            List<String> codeLocations = projectFacade.getProperties().getOriginalDirSrc();

            for (String source : codeLocations) {
                codeLocation += source + File.pathSeparator;
            }
        } else {
//            codeLocation = projectFacade.getInDirWithPreFix();
        }

//        String bytecodeLocation = projectFacade.getOutDirWithPrefix();
//        String classpath = projectFacade.getProperties().getDependenciesString();
//        String [] classpathArray = (classpath != null && !classpath.trim().isEmpty())
//                ? classpath.split(File.pathSeparator) : null;
//
//        try {
//            buildModel(codeLocation, bytecodeLocation, classpathArray);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 构建spoon 模型
     * @param srcPath
     * @param classpath
     */
    public void buildModel(String srcPath, String[] classpath) {
        buildModel(srcPath, null, classpath);
    }

    /**
     * 构建spoon模型
     * @param srcPath
     * @param bytecodePath
     * @param classpath
     */
    public void buildModel(String srcPath, String bytecodePath, String[] classpath) {
        JDTBasedSpoonCompiler jdtBasedSpoonCompiler = null;

        factory.getEnvironment().setCommentEnabled(ConfigurationProperties.getPropertyBool(PropertyKey.KeepComments));
        factory.getEnvironment().setNoClasspath(ConfigurationProperties.getPropertyBool(PropertyKey.NoClasspathSpoon));
        factory.getEnvironment().setPreserveLineNumbers(ConfigurationProperties.getPropertyBool(PropertyKey.PreserveLineNumber));
        factory.getEnvironment().setIgnoreDuplicateDeclarations(true);

        jdtBasedSpoonCompiler = new JDTBasedSpoonCompiler(factory);

        String [] sources = srcPath.split(File.pathSeparator);
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
     * 获取spoon factory
     */
    public static Factory getFactory() {
        if (factory == null) {
            factory = createFactory();
            factory.getEnvironment().setLevel("OFF");
        }
        return factory;
    }

    /**
     * 创建spoon factory
     * @return
     */
    private static Factory createFactory() {
        Environment env = getEnvironment();
        Factory factory = new FactoryImpl(new DefaultCoreFactory(), env);

        return factory;
    }

    /**
     * 获取spoon environment
     * @return
     */
    public static Environment getEnvironment() {
        StandardEnvironment env = new StandardEnvironment();

        Integer complianceLevel = ConfigurationProperties.getPropertyInt(PropertyKey.JavaComplianceLevel);
        env.setComplianceLevel((complianceLevel > 2) ? complianceLevel : 3);
        env.setVerbose(false);
        env.setDebug(true);
        env.setTabulationSize(5);
        env.useTabulations(true);
        return env;
    }

    public OutputWritter getOutput() {
        return output;
    }

    public void setOutput(OutputWritter output) {
        this.output = output;
    }
}
