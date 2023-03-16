package core.solutionsearch;

import conf.ConfigurationProperties;
import core.detector.MethodCallDetector;
import core.detector.entity.MethodCallPoint;
import core.entity.*;
import core.faultlocation.entity.SuspiciousCode;
import core.manipulation.bytecode.entity.CompilationResult;
import core.migration.entity.MigrationPoint;
import core.migration.util.MigrationSupporter;
import core.setup.ProjectMigrationFacade;
import core.solutionsearch.population.PopulationController;
import core.solutionsearch.population.ProgramVariantFactory;
import core.solutionsearch.population.TestCaseFitnessBasedPopulationController;
import core.solutionsearch.spaces.operators.Operator;
import core.solutionsearch.spaces.operators.OperatorSelectionStrategy;
import core.solutionsearch.spaces.operators.TemplateOperator;
import core.solutionsearch.spaces.operators.searchbased.SearchBasedOperationSelectionStrategy;
import core.solutionsearch.spaces.operators.searchbased.SearchBasedOperatorSpace;
import core.template.diff.ApiElementBuilder;
import core.template.diff.entity.ApiElement;
import core.validation.entity.VariantValidationResult;
import org.apache.log4j.Logger;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtElement;
import util.TimeUtil;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class SearchBasedMigrationEngine extends CoreMigrationEngine {
    private static final Logger logger = Logger.getLogger(SearchBasedMigrationEngine.class.getName());

    // 用于控制如何生成下一代变体
    protected PopulationController populationController = null;
    // 用于针对变体生成突变操作
    protected OperatorSelectionStrategy operatorSelectionStrategy = null;

    /**
     * 构造方法
     * @param migrationSupporter 一个用于辅助创建抽象语法树的工具类
     * @param projectFacade      一个用于访问待迁移项目的门面类
     */
    public SearchBasedMigrationEngine(MigrationSupporter migrationSupporter, ProjectMigrationFacade projectFacade) {
        super(migrationSupporter, projectFacade);
    }

    /**
     * 迁移引擎的核心迁移逻辑实现在该子方法中，具体由子类实现
     *
     * @throws Exception
     */
    @Override
    public void startSearch() throws Exception {
        // 对于项目中的所有调用点，需要逐一处理
        for (ProgramVariant invocationToMigration : this.originalCallPointToMigrate) {
            // 初始时，将原始迁移点变体放到当前种群内
            this.variants.clear();
            this.variants.add(invocationToMigration);

            // 执行转换操作
            tryMigrate(invocationToMigration);

            if (this.solutions == null || this.solutions.isEmpty()) {
                logger.info("no solution found");
                return;
            } else {
                logger.info("found solution " + this.solutions.size());
            }

            // 暂时只迁移一个
            break;
        }
    }


    /**
     * 直接通过api推理对代码调用点进行转换迁移
     * @param originVariant
     * @return
     */
    private void tryMigrate(ProgramVariant originVariant) throws Exception {
        // 每个变体只有一个修改点，即方法调用点代码元素，直接取第一个修改点
        ModificationPoint modificationPoint = originVariant.getModificationPoints().get(0);
        CtElement originalElement = modificationPoint.getCodeElement();
        CtElement originalElementParent = originalElement.getParent();

        CtCodeElement newElement = MigrationSupporter.getFactory().Code().createCodeSnippetStatement(originalElement.toString());
        newElement.setParent(originalElementParent);

        // 先进行基础的模板转换操作
        String originApi = ConfigurationProperties.getProperty(ConfigurationProperties.ORIGIN_API);
        String targetApi = ConfigurationProperties.getProperty(ConfigurationProperties.TARGET_API);

        Operator op = new TemplateOperator(ApiElementBuilder.buildApiElement(originApi), ApiElementBuilder.buildApiElement(targetApi));
        OperatorInstance operatorInstance = new StatementOperatorInstance(modificationPoint, op, modificationPoint.getCodeElement(), newElement);

        // 应用转换操作
        // 此时originVariant中对应的model已经更新了
        boolean isApplied = operatorInstance.apply();

        // 保存当前世代的操作实例
        originVariant.putModificationInstance(0, operatorInstance);

        boolean canCompile = compile(originVariant, 0);
        if (!canCompile) {
            // 失败
            return;
        }

        // 直接验证初始转换后是否正确
        boolean isSolution = this.validateVariant(originVariant, 0);
        if (isSolution) {
            logger.info("solution found " + originVariant.getId());
            this.solutions.add(originVariant);
            this.saveVariant(originVariant);
            return;
        }

        // 将模板转换后的变体放入初始种群
        this.variants.clear();
        this.variants.add(originVariant);
        this.originalVariant = originVariant;

        // 迭代突变搜索正确变体补丁
        searchVariant();
    }

    public void searchVariant() throws Exception {
        // 起始时间
        Date startTime = new Date();
        // 最大执行时间
        final int maxMinutes = ConfigurationProperties.getPropertyInt(ConfigurationProperties.MAX_TIME);
        // 当前代数
        int generationExecuted = 1;
        boolean stopSearch = false;
        while (!stopSearch) {
            // 搜索代数达到最大值，则停止
            if (generationExecuted >= 100) {
                logger.warn("max generation reached " + generationExecuted);
                break;
            }
            // 判断是否执行时间超过最大执行时间
            if (!TimeUtil.blowMaxTime(startTime, maxMinutes)) {
                logger.debug("max time reached" + generationExecuted);
                break;
            }


            // 在当前世代变体中寻找正确补丁，并生成下一代变体
            boolean solutionFound = processGenerations(generationExecuted);
            if (solutionFound) {
                stopSearch = true;
            }

            generationExecuted++;
        }
    }

    /**
     * 对变体进行编译，返回是否编译成功
     * @param variant
     * @param generation
     * @return
     * @throws MalformedURLException
     */
    public boolean compile(ProgramVariant variant, int generation) throws MalformedURLException {
        URL[] originalURL = projectFacade.getClassPathURLForProgramVariant(ProgramVariant.DEFAULT_ORIGINAL_VARIANT);
        CompilationResult compilationResult = compiler.compile(variant, originalURL);

        // 判断是否编译成功
        boolean childCompiles = compilationResult.compiles();
        // 保存编译结果
        variant.setCompilation(compilationResult);
        // 保存程序变体中的类
        storeModifiedVariantModel(variant);

        return childCompiles;
    }

    /**
     * 执行单元测试验证变体是否正确
     * @param variant
     * @param generation
     * @return
     * @throws Exception
     */
    public boolean validate(ProgramVariant variant, int generation) throws Exception {
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
        return false;
    }

    /**
     * 故障定位，获取可能存在故障的代码
     * @return
     * @throws Exception
     */
    public List<SuspiciousCode> calculateSuspicious() throws Exception {
        // Find tests
        List<String> regressionTestForFaultLocalization = this.getFaultLocalizationStrategy()
                .findTestCasesToExecute(projectFacade);
        projectFacade.getProjectConfiguration().setRegressionTestCases(regressionTestForFaultLocalization);

        logger.info("Test retrieved from classes: " + regressionTestForFaultLocalization.toString());

        List<SuspiciousCode> suspiciousCodeList = this.getFaultLocalizationStrategy()
                .searchSuspicious(projectFacade, regressionTestForFaultLocalization).getCandidates();

        return suspiciousCodeList;
    }

    @Override
    public void initPopulation() {
        List<MigrationPoint> points = extractMigrationPoints();
        this.originalCallPointToMigrate = new ArrayList<>();
        for (MigrationPoint p : points) {
            ProgramVariant invocationToMigrate = variantFactory.createProgramVariant(fromMigrationPoint(p), this.projectFacade);
            this.originalCallPointToMigrate.add(invocationToMigrate);
        }
    }

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
            mp.setCtClass(ProgramVariantFactory.getCtClassFromCtElement(p.callPoint));
            return mp;
        }).collect(Collectors.toList());

        return migrationPoints;
    }

    private List<ModificationPoint> fromMigrationPoint(List<MigrationPoint> points) {
        List<ModificationPoint> modificationPoints = new ArrayList<>();

        for (MigrationPoint p : points) {
            ModificationPoint mp = new ModificationPoint();
            mp.setCodeElement(p.getCodeElement());
            mp.setCtClass(p.getCtClass());
            modificationPoints.add(mp);
        }

        return modificationPoints;
    }

    private ModificationPoint fromMigrationPoint(MigrationPoint point) {
        ModificationPoint mp = new ModificationPoint();
        mp.setCodeElement(point.getCodeElement());
        mp.setCtClass(point.getCtClass());
        return mp;
    }

    @Override
    public void init() throws Exception {
        super.init();
        this.initPopulationController();
        this.initOperationSelectionStrategy();
    }

    public void initPopulationController() {
        this.populationController = new TestCaseFitnessBasedPopulationController();
    }

    public void initOperationSelectionStrategy() {
        this.operatorSelectionStrategy = new SearchBasedOperationSelectionStrategy(new SearchBasedOperatorSpace());
    }

    /**
     * 根据可疑代码初始化种群
     * @param suspicious
     */
    public void initPopulation(List<SuspiciousCode> suspicious) {
        initializePopulation(suspicious);
    }

    protected void initializePopulation(List<SuspiciousCode> suspicious) {
        this.variants = variantFactory.createInitialPopulation(suspicious, projectFacade);
        this.originalVariant = this.variants.get(0);
    }

    /**
     * 处理一个迭代版本i，循环处理所有实例
     * @param generation 迭代版本
     * @return
     * @throws Exception
     */
    private boolean processGenerations(int generation) throws Exception {
        // 产生变体
        // 分别计算适应度
        // 过滤无效变体
        // 分别验证
        // 验证通过加入集合
        boolean solutionFound = false;
        // 保存当前世代的程序变体
        List<ProgramVariant> tempralVariants = new ArrayList<>();
        // 遍历上一世代的变体，进行突变，生成新变体
        for (ProgramVariant parentVariant : this.variants) {
            ProgramVariant newVariant = createNewProgramVariant(parentVariant, generation);

            if (newVariant == null) {
                continue;
            }

            boolean solution = false;
            tempralVariants.add(newVariant);
            // 验证当前新变体
            solution = processCreatedVariant(newVariant, generation);

            // 撤销子变体中对程序model的更改
            reverseOperationInModel(newVariant, generation);

            if (solution) {
                // 找到正确补丁后，保存补丁，退出搜索
                solutionFound = true;
                this.solutions.add(newVariant);
//                this.saveVariant(newVariant);
                return solutionFound;
            }
        }

        // 如果当前世代没有找到补丁，则准备下一世代补丁的寻找
        prepareNextGeneration(tempralVariants, generation);

        return solutionFound;
    }

    /**
     * 撤销当前变体在程序model上产生的变更
     * @param variant
     * @param generation
     */
    public void reverseOperationInModel(ProgramVariant variant, int generation) {
        if (variant.getOperations() == null || variant.getOperations().isEmpty()) {
            return;
        }

        for (int genI = generation; genI >= 1; genI--) {
            undoSingleGeneration(variant, genI);
        }
    }

    protected void undoSingleGeneration(ProgramVariant instance, int genI) {
        List<OperatorInstance> operations = instance.getOperations().get(genI);
        if (operations == null || operations.isEmpty()) {
            return;
        }

        for (int i = operations.size() - 1; i >= 0; i--) {
            OperatorInstance genOperation = operations.get(i);
            genOperation.undo();
        }
    }

    /**
     * 创建一个自变体，如果没有应用突变，则返回null
     * @param parentVariant
     * @param generation
     * @return
     * @throws Exception
     */
    protected ProgramVariant createNewProgramVariant(ProgramVariant parentVariant, int generation) throws Exception {
        // 拷贝一份变体
        ProgramVariant childVariant = variantFactory.createProgramVariantFromAnother(parentVariant, generation);

        // 应用之前的操作，以获得最新的程序model
        applyPreviousOperationsToVariantModel(childVariant, generation);

        // 选择一个操作,在变体的当前世代中记录下该操作
        Operator operator = this.operatorSelectionStrategy.getNextOperator(childVariant.getModificationPoints().get(0));
        OperatorInstance operatorInstance = operator.createOperatorInstance(childVariant.getModificationPoints().get(0));
        childVariant.getOperations().put(generation, Arrays.asList(operatorInstance));

        // 将最新的操作应用到子变体上
        boolean appliedOperations = applyNewOperationsToVariantModel(childVariant, generation);

        if (!appliedOperations) {
            return null;
        }

        return childVariant;
    }

    /**
     * 将程序变体中记录的操作应用到程序模型上，使当前程序模型达到最新状态
     * @param variant
     * @param currentGeneration
     * @throws IllegalAccessException
     */
    public void applyPreviousOperationsToVariantModel(ProgramVariant variant, int currentGeneration) throws IllegalAccessException {
        // We do not include the current generation (should be empty)
        // 从2开始，因为1的操作是模板变更，为了简化，该操作对model的影响已经应用，当前model已经是更新后的了
        for (int generation_i = 1; generation_i < currentGeneration; generation_i++) {
            List<OperatorInstance> operations = variant.getOperations().get(generation_i);
            if (operations == null || operations.isEmpty()) {
                continue;
            }
            for (OperatorInstance genOperation : operations) {
                applyPreviousMutationOperationToSpoonElement(genOperation);
            }
        }
    }

    protected void applyPreviousMutationOperationToSpoonElement(OperatorInstance operation) {
        operation.apply();
    }


    /**
     *
     * Compiles and validates a created variant.
     *
     * @param generation
     * @return true if the variant is a solution. False otherwise.
     * @throws Exception
     */
    public boolean processCreatedVariant(ProgramVariant programVariant, int generation) throws Exception {
        // 尝试编译变体
        URL[] originalURL = projectFacade.getClassPathURLForProgramVariant(ProgramVariant.DEFAULT_ORIGINAL_VARIANT);
        CompilationResult compilation = compiler.compile(programVariant, originalURL);
        boolean childCompiles = compilation.compiles();
        programVariant.setCompilation(compilation);

        storeModifiedVariantModel(programVariant);

        // 编译成功，则进行单测验证
        if (childCompiles) {
            VariantValidationResult validationResult = validateInstance(programVariant);
            // 计算当前变体的适应度
            double fitness = this.fitnessFunction.calculateFitnessValue(validationResult);
            programVariant.setFitness(fitness);

            // 如果变体是正确补丁，则停止直接返回
            if (validationResult != null && validationResult.isSuccessful()) {
                saveVariant(programVariant);
                return true;
            }
        } else {
            // 编译失败，直接给变体赋值为最大适应度，后续被剔除
            programVariant.setFitness(this.fitnessFunction.getWorstMaxFitnessValue());
        }
        return false;
    }

    /**
     * 当当前世代的所有变体都处理完后，就产生了新的下一代种群，对新种群进行处理，并为下一世代的搜索做准备
     * @param temporalInstances
     * @param generation
     */
    public void prepareNextGeneration(List<ProgramVariant> temporalInstances, int generation) {
        // 如果当前还没有正确补丁，则继续下一代搜索
        this.variants = this.populationController.selectProgramVariantsForNextGeneration(variants, temporalInstances,
                ConfigurationProperties.getPropertyInt(ConfigurationProperties.POPULATION_SIZE),
                variantFactory, originalVariant, generation);
    }

    /**
     * 在当前世代应用突变操作
     * @param variant
     * @param currentGeneration
     * @throws IllegalAccessException
     */
    public boolean applyNewOperationsToVariantModel(ProgramVariant variant, int currentGeneration)
            throws IllegalAccessException {
        List<OperatorInstance> operations = variant.getOperations().get(currentGeneration);
        if (operations == null || operations.isEmpty()) {
            return false;
        }

        for (OperatorInstance genOperation : operations) {
            applyNewMutationOperationToSpoonElement(genOperation);
        }

        return true;
    }

    /**
     * Apply a given Mutation to the node referenced by the operation
     *
     * @param operationInstance
     * @throws IllegalAccessException
     */
    protected void applyNewMutationOperationToSpoonElement(OperatorInstance operationInstance) {
        operationInstance.apply();
    }

    /**
     * 验证程序变体能否通过单元测试
     * @param variant
     * @return
     */
    public VariantValidationResult validateInstance(ProgramVariant variant) {
        VariantValidationResult validationResult = this.variantValidator.validate(variant, projectFacade);
        if (validationResult != null) {
            variant.setIsSolution(validationResult.isSuccessful());
            variant.setValidationResult(validationResult);
        }
        return validationResult;
    }
}

