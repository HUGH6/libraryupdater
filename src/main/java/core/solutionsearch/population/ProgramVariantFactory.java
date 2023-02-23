package core.solutionsearch.population;

import core.entity.ModificationPoint;
import core.entity.ProgramVariant;
import core.entity.SuspiciousModificationPoint;
import core.faultlocation.entity.SuspiciousCode;
import core.migration.util.MigrationSupporter;
import core.setup.ProjectMigrationFacade;
import org.apache.log4j.Logger;
import spoon.reflect.cu.position.NoSourcePosition;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 创建初始的程序变体种群
 */
public class ProgramVariantFactory {
    protected static final Logger logger = Logger.getLogger(ProgramVariantFactory.class.getName());

    // id计数器，用于赋值给程序实例
    protected int idCounter = 0;

    protected MigrationSupporter migrationSupporter = null;

//    protected List<TargetElementProcessor<?>> processors = null;

    protected boolean resetOperations;

    protected ProjectMigrationFacade projectFacade;

    public ProgramVariantFactory() {
        super();
    }

//    public ProgramVariantFactory(Lsit<TargetElementProcessor<?>> processor) {
//        this();
//        this.processors = processor;
//    }

    /**
     * 拷贝可疑的Modification point
     * @param existingGen
     * @param modified
     * @return
     */
    public static SuspiciousModificationPoint clonePoint(SuspiciousModificationPoint existingGen, CtElement modified) {
        SuspiciousCode suspicious = existingGen.getSuspicious();
        CtClass<?> ctClass = existingGen.getCtClass();
        List<CtVariable<?>> context = existingGen.getContextOfModificationPoint();
        SuspiciousModificationPoint newGen = new SuspiciousModificationPoint(suspicious, modified, ctClass, context);
        return newGen;
    }

    /**
     * 拷贝Modification point
     * @param existingGen
     * @param modified
     * @return
     */
    public static ModificationPoint clonePoint(ModificationPoint existingGen, CtElement modified) {
        CtClass<?> ctClass = existingGen.getCtClass();
        List<CtVariable<?>> context = existingGen.getContextOfModificationPoint();
        ModificationPoint newGen = new ModificationPoint(modified, ctClass, context);
        return newGen;
    }

    public ProgramVariant createProgramVariant(List<ModificationPoint> points, ProjectMigrationFacade facade) {
        this.projectFacade = facade;
        ProgramVariant variant = createProgramInstance(idCounter++, points);
        return variant;
    }

    public ProgramVariant createProgramVariant(ModificationPoint point, ProjectMigrationFacade facade) {
        this.projectFacade = facade;

        ProgramVariant variant = new ProgramVariant(idCounter++);
        // 包含了待修改点
        variant.addModificationPoints(Arrays.asList(point));
        for (int i = 0; i < variant.getModificationPoints().size(); i++) {
            ModificationPoint mp = variant.getModificationPoints().get(i);
            mp.identified = i;
        }

        getAllCtClass(variant);
        return variant;
    }

    public ProgramVariant createProgramInstance(int programInstanceId, List<ModificationPoint> points) {
        ProgramVariant variant = new ProgramVariant(programInstanceId);

        // 包含了待修改点
        if (points != null && !points.isEmpty()) {
            variant.addModificationPoints(points);
        }

        for (int i = 0; i < variant.getModificationPoints().size(); i++) {
            ModificationPoint mp = variant.getModificationPoints().get(i);
            mp.identified = i;
//            resolveCtClass(mp.getCtClass().getQualifiedName(), variant);
        }

        getAllCtClass(variant);

        return variant;
    }

    private void getAllCtClass(ProgramVariant variant) {
        List<CtClass> classesFromModel = MigrationSupporter.getFactory().Class().getAll().stream()
                .filter(CtClass.class::isInstance).map(sc -> (CtClass) sc).collect(Collectors.toList());
        for (CtClass ctclasspointed : classesFromModel) {
            if (!variant.getBuiltClasses().containsKey(ctclasspointed.getQualifiedName())) {
                // TODO: clone or not?
                // CtClass ctclasspointed = getCtClassCloned(className);
                variant.getBuiltClasses().put(ctclasspointed.getQualifiedName(), ctclasspointed);
            }
        }
    }

    public static CtClass getCtClassFromCtElement(CtElement element) {
        if (element == null)
            return null;
        if (element instanceof CtClass)
            return (CtClass) element;
        return getCtClassFromCtElement(element.getParent());
    }

    public CtClass resolveCtClass(String className, ProgramVariant progInstance) {
        // if the ctclass exists in the cache, return it.
        if (progInstance.getBuiltClasses().containsKey(className)) {
            return progInstance.getBuiltClasses().get(className);
        }

        CtClass ctclasspointed = getCtClassFromName(className);
        if (ctclasspointed == null)
            return null;
        // Save the CtClass in cache
        progInstance.getBuiltClasses().put(className, ctclasspointed);

        return ctclasspointed;
    }

    @SuppressWarnings({ "static-access", "rawtypes" })
    public CtClass getCtClassFromName(String className) {

        CtType ct = MigrationSupporter.getFactory().Type().get(className);
        if (!(ct instanceof CtClass)) {
            return null;
        }

        return (CtClass) ct;
    }

    public List<ProgramVariant> createInitialPopulation(List<SuspiciousCode> suspicious, ProjectMigrationFacade projectFacade) {
        List<ProgramVariant> variants = new ArrayList<>();
        for (int ins = 1; ins < 100; ins++) {
            idCounter = ins;
            ProgramVariant v_i = createProgramInstance(suspicious, idCounter);
            variants.add(v_i);
        }
        return variants;
    }

    private ProgramVariant createProgramInstance(List<SuspiciousCode> suspiciousCodeList, int idProgramInstance) {
        ProgramVariant programInstance = new ProgramVariant(idProgramInstance);

        if (!suspiciousCodeList.isEmpty()) {
            for (SuspiciousCode suspiciousCode : suspiciousCodeList) {
                List<SuspiciousModificationPoint> modificationPoints = createModificationPoints(suspiciousCode, programInstance);
                if (modificationPoints != null && !modificationPoints.isEmpty()) {
                    programInstance.addModificationPoints(modificationPoints);
                }
            }

        } else {
//            List<SuspiciousModificationPoint> pointsFromAllStatements = createModificationPoints(programInstance);
//            programInstance.getModificationPoints().addAll(pointsFromAllStatements);
        }

        for (int i = 0; i < programInstance.getModificationPoints().size(); i++) {
            ModificationPoint mp = programInstance.getModificationPoints().get(i);
            mp.identified = i;
        }
        return programInstance;
    }

    /**
     * It receives a suspicious code (a line) and it create a list of Gens from than
     * suspicious line when it's possible.
     *
     * @param suspiciousCode
     * @param progInstance
     * @return
     */
    private List<SuspiciousModificationPoint> createModificationPoints(SuspiciousCode suspiciousCode,
                                                                       ProgramVariant progInstance) {

        List<SuspiciousModificationPoint> suspiciousModificationPoints = new ArrayList<SuspiciousModificationPoint>();

        CtClass ctclasspointed = resolveCtClass(suspiciousCode.getClassName(), progInstance);
        if (ctclasspointed == null) {
            logger.info(" Not ctClass for suspicious code " + suspiciousCode);
            return null;
        }

        List<CtElement> ctSuspects = null;
        try {
            ctSuspects = retrieveCtElementForSuspectCode(suspiciousCode, ctclasspointed);
            // The parent first, so I inverse the order
            Collections.reverse(ctSuspects);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        // if we are not able to retrieve suspicious CtElements, we return
        if (ctSuspects.isEmpty()) {
            return null;
        }

//        List<CtVariable> contextOfPoint = null;
        // We take the first element for getting the context (as the remaining
        // have the same location, it's not necessary)

//        contextOfPoint = VariableResolver.searchVariablesInScope(ctSuspects.get(0));

        // From the suspicious CtElements, there are some of them we are
        // interested in.
        // We filter them using the processors
        List<CtElement> filterByType = extractChildElements(ctSuspects, null);//processors);

        List<CtElement> filteredTypeByLine = intersection(filterByType, ctSuspects);

        // remove the elements that are instance of NoSourcePosition
        filteredTypeByLine = filteredTypeByLine.stream()
                .filter(ctElement -> !(ctElement.getPosition() instanceof NoSourcePosition))
                .collect(Collectors.toList());
        // For each filtered element, we create a ModificationPoint.
        for (CtElement ctElement : filteredTypeByLine) {
            SuspiciousModificationPoint modifPoint = new SuspiciousModificationPoint();
            modifPoint.setSuspicious(suspiciousCode);
            modifPoint.setCtClass(ctclasspointed);
            modifPoint.setCodeElement(ctElement);
//            modifPoint.setContextOfModificationPoint(contextOfPoint);
            suspiciousModificationPoints.add(modifPoint);

//            log.debug("--ModifPoint:" + ctElement.getClass().getSimpleName() + ", suspValue "
//                    + suspiciousCode.getSuspiciousValue() + ", line " + ctElement.getPosition().getLine() + ", file "
//                    + ((ctElement.getPosition().getFile() == null) ? "-null-file-"
//                    : ctElement.getPosition().getFile().getName()));
        }
        return suspiciousModificationPoints;
    }

    public List<CtElement> retrieveCtElementForSuspectCode(SuspiciousCode candidate, CtElement ctclass)
            throws Exception {
//        SpoonLocationPointerLauncher muSpoonLaucher = new SpoonLocationPointerLauncher(MutationSupporter.getFactory());
//        List<CtElement> susp = muSpoonLaucher.run(ctclass, candidate.getLineNumber());

//        return susp;
        return null;
    }

    @SuppressWarnings(value = { "unchecked", "rawtypes" })
    private List<CtElement> extractChildElements(List<CtElement> ctSuspects,
                                                 List<TargetElementProcessor<?>> processors) {

        if (processors == null || processors.isEmpty()) {
            return ctSuspects;
        }

        List<CtElement> ctMatching = new ArrayList<CtElement>();

        //            CodeParserLauncher spaceProcessor = new CodeParserLauncher(processors);
        for (CtElement element : ctSuspects) {
            List<CtElement> result = null;//spaceProcessor.createFixSpace(element, false);

            for (CtElement ctElement : result) {
                if (ctElement.toString().equals("super()")) {
                    continue;
                }
                if (!ctMatching.contains(ctElement))
                    ctMatching.add(ctElement);
            }
        }

        return ctMatching;
    }

    public <T> List<T> intersection(List<T> list1, List<T> list2) {
        List<T> list = new ArrayList<T>();

        for (T t : list1) {
            try {
                if (list2.contains(t)) {
                    list.add(t);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return list;
    }

    /**
     * 从一个已有的变体克隆一个新变体
     * @param parentVariant
     * @param generation
     * @return
     */
    public ProgramVariant createProgramVariantFromAnother(ProgramVariant parentVariant, int generation) {
        idCounter++;
        return this.createProgramVariantFromAnother(parentVariant, idCounter, generation);
    }

    /**
     * 从一个已有的变体克隆一个新变体
     * @param parentVariant
     * @param id
     * @param generation
     * @return
     */
    public ProgramVariant createProgramVariantFromAnother(ProgramVariant parentVariant, int id, int generation) {
        ProgramVariant childVariant = new ProgramVariant(id);
        childVariant.setGenerationSource(generation);
        childVariant.setParent(parentVariant);
        childVariant.getOperations().putAll(parentVariant.getOperations());
        childVariant.addModificationPoints(parentVariant.getModificationPoints());
        childVariant.getBuiltClasses().putAll(parentVariant.getBuiltClasses());
        childVariant.setFitness(parentVariant.getFitness());
        childVariant.setValidationResult(parentVariant.getValidationResult());
        return childVariant;
    }

}
