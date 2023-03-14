package core;

import conf.ConfigurationProperties;
import conf.ProjectConfiguration;
import core.detector.MethodCallDetector;
import core.detector.entity.MethodCallPoint;
import core.entity.ExecutionResult;
import core.entity.ProgramVariant;
import core.migration.entity.MigrationPoint;
import core.migration.util.MigrationSupporter;
import core.setup.ProjectMigrationFacade;
import core.template.diff.ApiElementBuilder;
import core.template.diff.entity.ApiElement;
import org.apache.log4j.Logger;
import spoon.Launcher;
import spoon.OutputType;
import spoon.SpoonModelBuilder.InputType;
import spoon.reflect.CtModel;
import util.MavenUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 执行迁移的入口程序框架
 *
 * 整个迁移过程如下：
 * 1. 指定待迁移的api和目标api（源目标api和目标api需要指定）
 * 2. 加载项目，检测api调用点（项目路径和依赖需要指定）
 * 3. 选择调用点，执行转换过程（各种转换得到的临时代码编译后的代码需要保存，因此需要指定位置）
 * 4. 对生成代码进行测试回归验证（测试用例位置需要指定）
 */
public abstract class AbstractMain {
    protected static Logger logger = Logger.getLogger(AbstractMain.class.getSimpleName());

    // 存储项目中所有的待迁移代码位置
    protected List<MigrationPoint> migrationPointList = new ArrayList<>();
    // 存放当前迁移项目门面类，通过一个Facade类来对项目信息的访问管理进行封装
    protected ProjectMigrationFacade migrationFacade;
    // 存放当前迁移项目的信息
    protected ProjectConfiguration projectConfiguration;

    /**
     * 初始化待迁移的软件项目信息
     * 需要最先执行
     */
    public void initProject() throws IOException {
        // 将待迁移项目所有依赖的jar包下载到该项目根目录下的lib目录
        downloadDependenciesJar();

        // ProjectConfiguration中的信息需要单独加载
        this.projectConfiguration = getProjectConfiguration();
        this.migrationFacade = new ProjectMigrationFacade(this.projectConfiguration);
        this.migrationFacade.setupWorkingDirectories(ProgramVariant.DEFAULT_ORIGINAL_VARIANT);

        // 编译项目
        compileProject();

        // 清空待迁移API调用点列表
        this.migrationPointList.clear();
    }

    /**
     * 执行迁移过程，模板方法，内部调用run方法执行实际迁移过程
     * 执行过程中涉及的参数全部通过配置文件的方式进行传递读取，避免通过命令行参数的解析
     * @return 返回迁移结果
     */
    public  ExecutionResult execute() throws Exception {
        return run();
    }

    /**
     * 对需要迁移的代码点执行迁移，需要在子类中实现实际迁移方法
     * @return 返回迁移结果
     * @throws Exception 可能抛出的异常
     */
    protected abstract ExecutionResult run() throws Exception;

    /**
     * 获得包含当前待迁移项目信息的配置配置类
     * @return
     */
    protected ProjectConfiguration getProjectConfiguration() {
        ProjectConfiguration pc = new ProjectConfiguration();

        // 配置待迁移项目的相关配置信息
        pc.setWorkingDirRoot(ConfigurationProperties.getProperty(ConfigurationProperties.WORKING_DIR_ROOT));
        pc.setWorkingDirForSourceCode(ConfigurationProperties.getProperty(ConfigurationProperties.WORKING_DIR_SOURCE_CODE));
        pc.setWorkingDirForBytecode(ConfigurationProperties.getProperty(ConfigurationProperties.WORKING_DIR_BYTECODE));

        String projectRoot = ConfigurationProperties.getProperty(ConfigurationProperties.LOCATION) + File.separator;
        pc.setOriginalDirProjectRoot(projectRoot);
        pc.setOriginalDirSrc(Arrays.asList(projectRoot + ConfigurationProperties.getProperty(ConfigurationProperties.ORIGINAL_DIR_SRC)));
        pc.setOriginalDirBin(Arrays.asList(projectRoot + ConfigurationProperties.getProperty(ConfigurationProperties.ORIGINAL_DIR_BIN)));
        pc.setOriginalDirTestSrc(Arrays.asList(projectRoot + ConfigurationProperties.getProperty(ConfigurationProperties.ORIGINAL_DIR_TEST)));
        pc.setOriginalDirTestBin(Arrays.asList(projectRoot + ConfigurationProperties.getProperty(ConfigurationProperties.ORIGINAL_DIR_TEST_BIN)));
        pc.setDependencies(ConfigurationProperties.getProperty(ConfigurationProperties.DEPENDENCIES_PATH));
        pc.setOriginalDirData(projectRoot + ConfigurationProperties.getProperty(ConfigurationProperties.ORIGIN_DIR_DATA));

        pc.setFailingTestCases(new ArrayList<>());
        // pc.setRegressionTestCases(ConfigurationProperties.getProperty(ConfigurationProperties.ORIGINAL_DIR_TEST));

        return pc;
    }

    /**
     * 对原始的项目代码进行编译，生成字节码
     */
    protected void compileProject() {
        if (this.projectConfiguration == null) {
            throw new IllegalStateException("please init project first");
        }

        Launcher launcher = new Launcher();

        for (String srcPath : projectConfiguration.getOriginalDirSrc()) {
            logger.debug("add folder to compile: " + srcPath);
            launcher.addInputResource(srcPath);
        }

        for (String testPath : projectConfiguration.getOriginalDirTestSrc()) {
            logger.debug("add folder to compile: " + testPath);
            launcher.addInputResource(testPath);
        }

        // 设置编译字节码输出路径
        String compileOutputPath = projectConfiguration.getWorkingDirForBytecode()
                + File.separator
                + ProgramVariant.DEFAULT_ORIGINAL_VARIANT;
        launcher.setBinaryOutputDirectory(compileOutputPath);

        logger.info("compile original source code from "
                + launcher.getModelBuilder().getInputSources()
                + "\n"
                + "byte code saved in "
                + launcher.getModelBuilder().getBinaryOutputDirectory());

        launcher.getEnvironment().setPreserveLineNumbers(true);
        launcher.getEnvironment().setComplianceLevel(8);
        launcher.getEnvironment().setShouldCompile(true);
        launcher.getEnvironment().setSourceClasspath(projectConfiguration.getDependenciesString().split(File.pathSeparator));
        launcher.buildModel();
        launcher.getModelBuilder().generateProcessedSourceFiles(OutputType.COMPILATION_UNITS);
        launcher.getModelBuilder().compile(InputType.FILES);

        // 记录编译后的字节码目录
        migrationFacade.getProjectConfiguration().setOriginalDirBin(
                Collections.singletonList(launcher.getModelBuilder().getBinaryOutputDirectory().getAbsolutePath()));
        // 记录编译后的单测代码目录
        migrationFacade.getProjectConfiguration().setOriginalDirTestBin(
                Collections.singletonList(launcher.getModelBuilder().getBinaryOutputDirectory().getAbsolutePath()));
    }

    /**
     * 从项目中提取所有的待迁移api调用点
     * 必须等构建了model了之后才能进行提取
     * @return
     */
    protected List<MigrationPoint> extractMigrationPoints() {
        CtModel model = MigrationSupporter.getFactory().getModel();
        if (model == null) {
            throw new IllegalStateException("please build spoon model first");
        }

        String originApi = ConfigurationProperties.getProperty(ConfigurationProperties.ORIGIN_API);
        ApiElement originApiElement = ApiElementBuilder.buildApiElement(originApi);
        List<MethodCallPoint> callPoints = MethodCallDetector.detectMethodCall(model, originApiElement);

        // 格式转换
        List<MigrationPoint> migrationPoints = callPoints.stream().map(p -> {
            MigrationPoint mp = new MigrationPoint();
            mp.setCodeElement(p.callPoint);
            return mp;
        }).collect(Collectors.toList());

        // 保存到成员变量
        this.migrationPointList = migrationPoints;

        return migrationPoints;
    }



    /**
     * 下载项目依赖的jar包到根目录lib目录下
     * @throws IOException
     */
    protected void downloadDependenciesJar() throws IOException {
        ProcessBuilder builder = new ProcessBuilder();

        String mvnCommand = MavenUtil.getMvnCommand();
        if (mvnCommand == null || mvnCommand.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "To download dependencies please add the maven command in your path or add it to the property 'mvndir'");
        }

        String dependencyDownloadCommand = "dependency:copy-dependencies";
        // 默认将依赖jar包放到项目根目录下的lib目录下
        builder.command("cmd.exe", "/c", mvnCommand, dependencyDownloadCommand, "-DoutputDirectory=lib");

        builder.directory(new File(ConfigurationProperties.getProperty(ConfigurationProperties.LOCATION)));

        Process process = builder.start();
    }

    public static void main(String[] args) throws Exception {
        AbstractMain e = new AbstractMain() {
            @Override
            protected ExecutionResult run() throws Exception {
                return null;
            }
        };
        e.initProject();

        MigrationSupporter.getSupporter().buildSpoonModel(e.migrationFacade);
        String api = ConfigurationProperties.getProperty(ConfigurationProperties.ORIGIN_API);
        List<MigrationPoint> points = e.extractMigrationPoints();

        System.out.println(points);
    }
}
