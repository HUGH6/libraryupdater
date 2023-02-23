package core.entity;

import conf.ConfigurationProperties;
import core.manipulation.bytecode.entity.CompilationResult;
import core.setup.PropertyKey;
import core.validation.entity.VariantValidationResult;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;

import java.util.*;

/**
 * 程序的变体，变体可能会随时间而变化。
 *
 * 每个程序变体都包含一个“Gen”（可以被修改用于寻找patch的statements）列表，它表示在整个演变过程中可以针对该变体修改的所有位置（即语句）。
 * 对于单点修复，这些gens中只有一个受到突变算子的影响。
 * 在多点修复的情况下,来自 ProgramVariant 的不同gens可以通过突变运算符修改。
 * 请注意，2 个gens可以在同一代中修改 :
 * a）在同一个演化代数中
 * b）在不同的世代中（因此变体会随着时间的推移而变化）。
 * 例如，在 X 世代中，您修改了位置 i 处的 gen，而在 X+1 代中，您修改了位置 j。
 * ProgramVariant 包含一个跟踪历史记录的 Map “operations”，即在每一代中对 gens 执行的操作。
 */
public class ProgramVariant {
    // 原始突变体的标识
    public static final String DEFAULT_ORIGINAL_VARIANT = "default";

    // 变体id
    protected int id = 0;
    // 程序的gens（可以被用于修改来寻找patch的statements）列表
    protected List<ModificationPoint> modificationPoints = null;
    // 从spoon模型中加载的类引用，这些类被所有的变体共享
    protected Map<String, CtClass> loadClasses = new HashMap<>();
    // 应用到修改点的操作，按照世代组织
    protected Map<Integer, List<OperatorInstance>> operations = null;
    // parent variant
    protected ProgramVariant parent = null;
    // 该变体产生时的突变世代id
    protected int generationSource = 0;
    // 是否是解决方案
    protected boolean isSolution = false;
    // 包含受变体影响的类的列表，以及变体涉及的相应更改
    // 请注意，这些类是从共享模型中克隆的，并且仅属于此变体。这意味着此变体的子变体不引用这些实例
    protected List<CtClass> modifiedClasses = new ArrayList<>();
    // 存储当前变体的编译结果
    protected CompilationResult compilationResult = null;
    // 存储当前变体的验证结果
    protected VariantValidationResult validationResult = null;
    // 程序变体的适应度
    protected double fitness = Double.MAX_VALUE;

    public ProgramVariant() {
        this.modificationPoints = new ArrayList<>();
        this.operations = new HashMap<>();
    }

    public ProgramVariant(int id) {
        this();
        this.id = id;
    }

    public List<ModificationPoint> getModificationPoints() {
        return modificationPoints;
    }

    public void setModificationPoints(List<ModificationPoint> modificationPoints) {
        this.modificationPoints = modificationPoints;
    }

    public Map<String, CtClass> getBuiltClasses() {
        return loadClasses;
    }

    public List<CtClass> getModifiedClasses() {
        return modifiedClasses;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void putModificationInstance(int generation, OperatorInstance op) {
        List<OperatorInstance> modificationPoints = operations.get(generation);
        if (modificationPoints == null) {
            modificationPoints = new ArrayList<OperatorInstance>();
            operations.put(generation, modificationPoints);
        }
        modificationPoints.add(op);

    }

    public Map<Integer, List<OperatorInstance>> getOperations() {
        return operations;
    }

    public List<OperatorInstance> getAllOperations() {
        List<OperatorInstance> allops = new ArrayList<>();
        for (List<OperatorInstance> ops : this.operations.values()) {
            allops.addAll(ops);
        }
        return allops;
    }

    public List<OperatorInstance> getOperations(int generation) {
        if (!operations.containsKey(generation))
            operations.put(generation, new ArrayList<>());
        return operations.get(generation);
    }

    public ProgramVariant getParent() {
        return parent;
    }

    public void setParent(ProgramVariant parent) {
        this.parent = parent;
    }

    public int getGenerationSource() {
        return generationSource;
    }

    public void setGenerationSource(int generationSource) {
        this.generationSource = generationSource;
    }

    public CompilationResult getCompilation() {
        return compilationResult;
    }

    public void setCompilation(CompilationResult compilation) {
        this.compilationResult = compilation;
    }

    public void setIsSolution(boolean isSolution) {
        this.isSolution = isSolution;
    }

    public boolean isSolution() {
        return this.isSolution;
    }

    public void setValidationResult(VariantValidationResult result) {
        this.validationResult = result;
    }

    public VariantValidationResult getValidationResult() {
        return this.validationResult;
    }

    public void setOperations(Map<Integer, List<OperatorInstance>> operations) {
        this.operations = operations;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public void addModificationPoints(List<? extends ModificationPoint> points) {
        for (ModificationPoint p : points) {
            this.modificationPoints.add(p);
            p.setProgramVariant(this);
        }
    }

    @Override
    public String toString() {
        return "[Variant id: " + this.id + (this.isSolution ? " (SOL) " : "") + ", #gens: "
                + this.getModificationPoints().size() + ", #ops: " + this.operations.values().size() + ", parent:"
                + ((this.parent == null) ? "-" : this.parent.id) + "]";
    }

    /**
     * 返回被该变体影响的类
     * 这些类在所有的变体之间共享
     * @return
     */
    public List<CtType<?>> getAllClasses() {
        List<CtType<?>> result = new ArrayList<>();

        for (CtClass c : loadClasses.values()) {
            result.add(c);
        }

        return Collections.unmodifiableList(result);
    }

    public List<CtType<?>> getClassesAffectedByOperators() {
        Set<CtType<?>> affectedClassesSet = new HashSet<>();

        for (OperatorInstance opt : getAllOperations()) {
            ModificationPoint modifPoint = opt.getModificationPoint();
            affectedClassesSet.add(modifPoint.getCtClass());
        }

        List<CtType<?>> affectedClasses = new ArrayList<CtType<?>>(affectedClassesSet);

        return Collections.unmodifiableList(affectedClasses);
    }

    /**
     * 获取程序变体标识
     * 初始变体为default
     * 某个突变变体为variant-id
     * @return
     */
    public String currentMutatorIdentifier() {
        return (id >= 0) ? (ConfigurationProperties.getProperty(PropertyKey.VariantFolderPrefixName) + id) : DEFAULT_ORIGINAL_VARIANT;
    }
}
