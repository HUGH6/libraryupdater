package core.solutionsearch;

import conf.ConfigurationProperties;
import core.detector.MethodCallDetector;
import core.detector.entity.MethodCallPoint;
import core.entity.*;
import core.migration.entity.MigrationPoint;
import core.migration.util.MigrationSupporter;
import core.setup.ProjectMigrationFacade;
import core.solutionsearch.population.ProgramVariantFactory;
import core.solutionsearch.spaces.operators.Operator;
import core.solutionsearch.spaces.operators.TemplateOperator;
import core.template.diff.ApiElementBuilder;
import core.template.diff.entity.ApiElement;
import org.apache.log4j.Logger;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtElement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 默认迁移方法
 * 用于实现没有单测回归验证下的直接迁移
 */
public class DefaultEngine extends CoreMigrationEngine {
    private static final Logger logger = Logger.getLogger(DefaultEngine.class.getName());

    /**
     * 构造方法
     * @param migrationSupporter 一个用于辅助创建抽象语法树的工具类
     * @param projectFacade      一个用于访问待迁移项目的门面类
     */
    public DefaultEngine(MigrationSupporter migrationSupporter, ProjectMigrationFacade projectFacade) {
        super(migrationSupporter, projectFacade);
    }

    /**
     * 迁移引擎的核心迁移逻辑实现在该子方法中，具体由子类实现
     * @throws Exception
     */
    @Override
    public void startSearch() throws Exception {
        for (ProgramVariant invocationToMigrate : this.originalCallPointToMigrate) {
            logger.info("start migrate");
            this.variants.clear();
            this.variants.add(invocationToMigrate);

            tryMigrate(invocationToMigrate);

            if (this.solutions == null || this.solutions.isEmpty()) {
                logger.info("no solution found");
                return;
            } else {
                logger.info("found solution " + this.solutions.size());
                for (ProgramVariant patch : this.solutions) {
                    saveVariant(patch);
                }
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
        // 直接取第一个修改点
        ModificationPoint modificationPoint = originVariant.getModificationPoints().get(0);
        CtElement originalElement = modificationPoint.getCodeElement();
        CtElement originalElementParent = originalElement.getParent();

        CtCodeElement newElement = MigrationSupporter.getFactory().Code().createCodeSnippetStatement(originalElement.toString());
        newElement.setParent(originalElementParent);

        // 生成转换操作
        String originApi = ConfigurationProperties.getProperty(ConfigurationProperties.ORIGIN_API);
        String targetApi = ConfigurationProperties.getProperty(ConfigurationProperties.TARGET_API);

        Operator op = new TemplateOperator(ApiElementBuilder.buildApiElement(originApi), ApiElementBuilder.buildApiElement(targetApi));
        OperatorInstance operatorInstance = new StatementOperatorInstance(modificationPoint, op, modificationPoint.getCodeElement(), newElement);
        // 应用转换操作
        boolean isApplied = operatorInstance.apply();

        // 保存当前世代的操作实例
        originVariant.putModificationInstance(0, operatorInstance);
        // 设置新变体id，由于是单次转换，所以直接设置一个值
        originVariant.setId(0);

        // 验证是否有效
        boolean isSolution = this.validateVariant(originVariant, 0);
        if (isSolution) {
            logger.info("solution found " + originVariant.getId());
            this.solutions.add(originVariant);
            this.saveVariant(originVariant);
            return;
        }
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

    private ModificationPoint fromMigrationPoint(MigrationPoint point) {
        ModificationPoint mp = new ModificationPoint();
        mp.setCodeElement(point.getCodeElement());
        mp.setCtClass(point.getCtClass());
        return mp;
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

        // todo: 实现转换
        for (MigrationPoint p : points) {
            ModificationPoint mp = new ModificationPoint();
            mp.setCodeElement(p.getCodeElement());
            mp.setCtClass(p.getCtClass());
            modificationPoints.add(mp);
        }

        return modificationPoints;
    }
}
