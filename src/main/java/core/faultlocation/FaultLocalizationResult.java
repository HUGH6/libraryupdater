package core.faultlocation;

import core.faultlocation.entity.SuspiciousCode;

import java.util.List;

/**
 * 存储故障定位的结果
 */
public class FaultLocalizationResult {
    // 可疑代码列表
    private List<SuspiciousCode> candidates;
    // 失败的测试用例类
    private List<String> failingTestCasesClasses;
    // 失败的测试用例方法
    private List<String> failingTestCasesMethods;
    // 执行的测试用例方法
    private List<String> executedTestCasesMethods;

    public FaultLocalizationResult(List<SuspiciousCode> candidates) {
        super();
        this.candidates = candidates;
    }

    public FaultLocalizationResult(List<SuspiciousCode> candidates, List<String> failingTestCasesClasses) {
        super();
        this.candidates = candidates;
        this.failingTestCasesClasses = failingTestCasesClasses;
    }

    public FaultLocalizationResult(List<SuspiciousCode> candidates, List<String> failingTestCasesClasses,
                                   List<String> executedTestCasesMethods) {
        super();
        this.candidates = candidates;
        this.failingTestCasesMethods = failingTestCasesClasses;
        this.executedTestCasesMethods = executedTestCasesMethods;
    }

    public List<SuspiciousCode> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<SuspiciousCode> candidates) {
        this.candidates = candidates;
    }

    public List<String> getFailingTestCasesClasses() {
        return failingTestCasesClasses;
    }

    public void setFailingTestCasesClasses(List<String> failingTestCases) {
        this.failingTestCasesClasses = failingTestCases;
    }

    public List<String> getExecutedTestCasesMethods() {
        return executedTestCasesMethods;
    }

    public void setExecutedTestCasesMethods(List<String> executedTestCases) {
        this.executedTestCasesMethods = executedTestCases;
    }

    public List<String> getFailingTestCasesMethods() {
        return failingTestCasesMethods;
    }

    public void setFailingTestCasesMethods(List<String> failingTestCasesMethods) {
        this.failingTestCasesMethods = failingTestCasesMethods;
    }

    @Override
    public String toString() {
        return "FaultLocalizationResult{" + "candidates=" + candidates + ", failingTestCases=" + failingTestCasesClasses
                + '}';
    }
}
