package core.faultlocation;

import core.setup.ProjectMigrationFacade;

import java.util.List;

public interface FaultLocalizationStrategy {
    /**
     * 进行故障定位，搜索可疑的故障语句
     * @param facade
     * @param testToRun
     * @return
     */
    public FaultLocalizationResult searchSuspicious(ProjectMigrationFacade facade, List<String> testToRun) throws Exception;

    /**
     * 加载用于故障定位的测试用例
     * @param facade
     * @return
     */
    public List<String> findTestCasesToExecute(ProjectMigrationFacade facade);
}
