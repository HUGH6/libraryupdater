package main;

import conf.ConfigurationProperties;
import core.AbstractMain;
import core.entity.ExecutionResult;
import core.migration.util.MigrationSupporter;
import core.solutionsearch.CoreMigrationEngine;
import core.solutionsearch.DefaultEngine;
import core.solutionsearch.SearchBasedMigrationEngine;
import org.apache.log4j.Logger;

import java.util.List;

public class MigrationMain extends AbstractMain {
    private static Logger logger = Logger.getLogger(MigrationMain.class.getName());

    // 用于执行代码迁移的迁移引擎
    protected CoreMigrationEngine engine = null;

    /**
     * 对需要迁移的代码点执行迁移，需要在子类中实现实际迁移方法
     *
     * @return 返回迁移结果
     * @throws Exception 可能抛出的异常
     */
    @Override
    protected ExecutionResult run() throws Exception {
        // 需要先init project，完成对各种项目配置信息的初始化
        initProject();

        String migrationModeStr = ConfigurationProperties.getProperty(ConfigurationProperties.MIGRATION_MODE).toUpperCase();
        ExecutionMode migrationMode = ExecutionMode.valueOf(migrationModeStr);

        // 会对迁移引擎所需的组件进行初始化
        // 同时，会对项目模型进行构建，后续模型可以从MigrationSupporter中获取
        createEngine(migrationMode);

        engine.startSearch();

        engine.atEnd();

        return null;
    }

    public CoreMigrationEngine createEngine(ExecutionMode mode) throws Exception {
        MigrationSupporter supporter = MigrationSupporter.getSupporter();

        switch (mode) {
            case DEFAULT:
                this.engine = new DefaultEngine(supporter, migrationFacade);
                break;
            case GENERATE_VALIDATE:
                this.engine = new SearchBasedMigrationEngine(supporter, migrationFacade);
                break;
            default:
                this.engine = new DefaultEngine(supporter, migrationFacade);
        }

        this.engine.init();

        this.engine.initModel();

        this.engine.initPopulation();

        List<String> testCases = this.engine.getFaultLocalizationStrategy().findTestCasesToExecute(migrationFacade);
        migrationFacade.getProjectConfiguration().setRegressionTestCases(testCases);

        return this.engine;
    }

    public static void main(String[] args) throws Exception {
        MigrationMain main = new MigrationMain();
        main.execute();

        System.out.println("done");
    }
}
