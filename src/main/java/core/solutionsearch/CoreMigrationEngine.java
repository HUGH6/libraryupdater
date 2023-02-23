package core.solutionsearch;

import conf.ConfigurationProperties;
import core.FaultLocalizationMain;
import core.entity.OperatorInstance;
import core.entity.ProgramVariant;
import core.faultlocation.FaultLocalizationFactory;
import core.faultlocation.FaultLocalizationStrategy;
import core.manipulation.bytecode.compiler.SpoonClassCompiler;
import core.manipulation.bytecode.compiler.VariantCompiler;
import core.manipulation.bytecode.entity.CompilationResult;
import core.migration.util.MigrationSupporter;
import core.setup.ProjectMigrationFacade;
import core.solutionsearch.population.FitnessFunction;
import core.solutionsearch.population.ProgramVariantFactory;
import core.solutionsearch.population.TestCaseFitnessFunction;
import core.validation.ProgramVariantValidator;
import core.validation.ValidatorFactory;
import core.validation.entity.ValidatorTypeEnum;
import core.validation.entity.VariantValidationResult;
import org.apache.log4j.Logger;
import spoon.reflect.declaration.CtClass;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 实现api迁移的整体框架，是迁移引擎的核心框架
 */
public abstract class CoreMigrationEngine {
    public static final Logger logger = Logger.getLogger(CoreMigrationEngine.class.getSimpleName());

    /**
     * 用于代码管理
     */
    // 用于支持代码抽象语法树转换的工具
    protected MigrationSupporter migrationSupporter = null;
    // 用于辅助访问待迁移项目信息的门面类
    protected ProjectMigrationFacade projectFacade = null;
    // 编译代码变体
    protected VariantCompiler compiler = null;
    // 用于验证程序变体是否正确
    protected ProgramVariantValidator variantValidator = null;
    // 用于故障定位
    protected FaultLocalizationStrategy faultLocalizationStrategy = null;
    // 用于评估代码变体适应度
    protected FitnessFunction fitnessFunction = null;

    /**
     * 转换数据与转换结果
     */
    // 保存初始的代码变体
    protected ProgramVariant originalVariant = null;
    // 所有待迁移的带调用点，每一个调用点都需要单独进行迁移处理
    protected List<ProgramVariant> originalCallPointToMigrate = new ArrayList<>();
    // 保存当前待迁移代码点的所有代码变体
    protected List<ProgramVariant> variants = new ArrayList<>();
    // 保存迁移成功的代码转换结果
    protected List<ProgramVariant> solutions = new ArrayList<>();
    // 用于创建ProgramVariant
    protected ProgramVariantFactory variantFactory = new ProgramVariantFactory();

    /**
     * 构造方法
     * @param migrationSupporter 一个用于辅助创建抽象语法树的工具类
     * @param projectFacade 一个用于访问待迁移项目的门面类
     */
    public CoreMigrationEngine(MigrationSupporter migrationSupporter, ProjectMigrationFacade projectFacade) {
        this.migrationSupporter = migrationSupporter;
        this.projectFacade = projectFacade;
    }

    /**
     * 迁移引擎的核心迁移逻辑实现在该子方法中，具体由子类实现
     * @throws Exception
     */
    public abstract void startSearch() throws Exception;

    /**
     * 用于在迁移过程结束后进行处理
     * @throws Exception
     */
    public void atEnd() throws Exception {
        if (this.solutions.isEmpty()) {
            System.out.println("no solution found");
        } else {
            String ids = "";
            for (ProgramVariant p : this.solutions) {
                ids += p.getId() + "\n";
            }
            System.out.println(this.solutions.size() + " solutions found.\nvariant-ids:\n" + ids);

            for (int gen = 0; gen <= this.solutions.get(0).getGenerationSource(); gen++) {
                OperatorInstance op =this.solutions.get(0).getOperations().get(gen).get(0);
                System.out.println(op.toString());
            }
        }
    }

    /**
     * 构建项目模型
     * 需要在Main方法中调用init Project方法后才能调用
     * @throws Exception
     */
    public void initModel() throws Exception {
        this.migrationSupporter.buildSpoonModel(this.projectFacade);
    }

    /**
     * 编译并验证代码变体
     * @return
     * @param variant
     * @param generation
     * @return
     * @throws Exception
     */
    public boolean validateVariant(ProgramVariant variant, int generation) throws Exception {
        // 编译代码变体
        // 获取代码变体的class path
        URL[] originalURL = projectFacade.getClassPathURLForProgramVariant(ProgramVariant.DEFAULT_ORIGINAL_VARIANT);

//        String sourceLibPath = ConfigurationProperties.getProperty(ConfigurationProperties.SOURCE_LIBRARY_PATH);
//        String targetLibPath = ConfigurationProperties.getProperty(ConfigurationProperties.TARGET_LIBRARY_PATH);
//        // 过滤
//        for (int i = 0; i < originalURL.length; i++) {
//            String originJarPath = originalURL[i].getPath();
//            String sourceJarPath = new File(sourceLibPath).toURL().getPath();
//
//            if (originJarPath.equals(sourceJarPath)) {
//                originalURL[i] = new File(targetLibPath).toURL();
//            }
//        }

        CompilationResult compilationResult = compiler.compile(variant, originalURL);

        // 判断是否编译成功
        boolean childCompiles = compilationResult.compiles();
        // 保存编译结果
        variant.setCompilation(compilationResult);
        // 保存程序变体中的类
        storeModifiedVariantModel(variant);

        // 检验代码变体
        if (childCompiles) {
            // 编译成功
            logger.debug("thid child compiles: " + variant.getId());

            // 检验变体是否正确，实际的检验方式在子类中实现
            VariantValidationResult validationResult = validate(variant);

            if (validationResult != null && validationResult.isSuccessful()) {
                logger.info("solution found, child variant: " + variant.getId());
                variant.setIsSolution(validationResult.isSuccessful());
                variant.setValidationResult(validationResult);
                // 保存代码变体到磁盘
                saveVariant(variant);
                return true;
            }
        } else {
            // 编译失败
            logger.debug("the child dose not compile: "
                    + variant.getId()
                    + ", errors: "
                    + compilationResult.getErrorList());
        }

        return false;
    }

    /**
     * 保存程序代码变体
     * @param variant
     * @throws Exception
     */
    public void saveVariant(ProgramVariant variant) throws Exception {
        String srcOutputPath = projectFacade.getMutatorInDirWithPrefix(variant.currentMutatorIdentifier());
        migrationSupporter.saveProgramVariantSourceCodeOnDisk(variant, srcOutputPath);
    }

    /**
     * 验证程序变体是否正确
     * @param variant
     * @return
     */
    protected VariantValidationResult validate(ProgramVariant variant) {
        return variantValidator.validate(variant, projectFacade);
    }

    /**
     * 在ProgramVariant中存储当前变体中涉及的每个 ctclass 的克隆。
     */
    protected void storeModifiedVariantModel(ProgramVariant variant) {
        variant.getModifiedClasses().clear();
        for (CtClass<?> modifiedClass : variant.getBuiltClasses().values()) {
            CtClass<?> cloneClass = (CtClass<?>) MigrationSupporter.clone(modifiedClass);
            cloneClass.setParent(modifiedClass.getParent());
            variant.getModifiedClasses().add(cloneClass);
        }
    }

    /**
     * 对使用的所有组件进行初始化
     * @throws Exception
     */
    public void init() throws Exception {
        this.initFaultLocalizationStrategy();
        this.initCompiler();
        this.initValidator();
        this.initFitnessFunction();
    }

    /**
     * 初始化故障定位组件
     * @throws Exception
     */
    protected void initFaultLocalizationStrategy() throws Exception {
        String faultLocalizationStr = ConfigurationProperties.getProperty(ConfigurationProperties.FAULT_LOCALIZATION_TYPE).toUpperCase();
        FaultLocalizationMain.FaultLocalizationTypeEnum faultLocalizationType = FaultLocalizationMain.FaultLocalizationTypeEnum.valueOf(faultLocalizationStr);
        this.setFaultLocalizationStrategy(FaultLocalizationFactory.getFaultLocalization(faultLocalizationType));
    }

    /**
     * 初始化编译器
     * @throws Exception
     */
    protected void initCompiler() throws Exception {
        this.setCompiler(new SpoonClassCompiler());
    }

    /**
     * 初始化程序变体验证器
     * @throws Exception
     */
    protected void initValidator() throws Exception {
        String validationTypeStr = ConfigurationProperties.getProperty(ConfigurationProperties.VALIDATION_TYPE).toUpperCase();
        ValidatorTypeEnum validatorType = ValidatorTypeEnum.valueOf(validationTypeStr);
        this.setVariantValidator(ValidatorFactory.createValidator(validatorType));
    }

    /**
     * 初始化适应度函数计算模块
     * @throws Exception
     */
    protected void initFitnessFunction() throws Exception {
        this.setFitnessFunction(new TestCaseFitnessFunction());
    }

    /***************************************
     * getter and setter
     **************************************/

    /**
     * 返回待迁移代码点的所有变体
     * @return
     */
    public List<ProgramVariant> getVariants() {
        return this.variants;
    }

    /**
     * 返回所有成功的转换代码
     * @return
     */
    public List<ProgramVariant> getSolutions() {
        return this.solutions;
    }

    public FaultLocalizationStrategy getFaultLocalizationStrategy() {
        return faultLocalizationStrategy;
    }

    public void setFaultLocalizationStrategy(FaultLocalizationStrategy faultLocalizationStrategy) {
        this.faultLocalizationStrategy = faultLocalizationStrategy;
    }

    public VariantCompiler getCompiler() {
        return compiler;
    }

    public void setCompiler(VariantCompiler compiler) {
        this.compiler = compiler;
    }

    public ProgramVariantValidator getVariantValidator() {
        return variantValidator;
    }

    public void setVariantValidator(ProgramVariantValidator variantValidator) {
        this.variantValidator = variantValidator;
    }

    public FitnessFunction getFitnessFunction() {
        return fitnessFunction;
    }

    public void setFitnessFunction(FitnessFunction fitnessFunction) {
        this.fitnessFunction = fitnessFunction;
    }

    public abstract void initPopulation();
}
