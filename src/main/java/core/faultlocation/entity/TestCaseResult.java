package core.faultlocation.entity;

/**
 * 表示一个测试用例
 */
public class TestCaseResult {
    // 测试用例全名
    private String testCaseCompleteName;
    // 测试用例名称
    private String testCaseName;
    // 测试用例的类
    private String testCaseClass;
    //
    private String trance;
    // 是否正确
    private Boolean correct;
    // 是否参数化
    private Boolean isParameterized = false;

    public TestCaseResult(String testCaseCompleteName, String testCaseName, String testCaseClass, Boolean correct) {
        super();
        this.testCaseCompleteName = testCaseCompleteName;
        this.testCaseName = testCaseName;
        this.testCaseClass = testCaseClass;
        this.correct = correct;
    }

    public TestCaseResult(String testCaseCompleteName, boolean correct) {
        super();
        this.testCaseCompleteName = testCaseCompleteName;
        this.correct = correct;

        if (testCaseCompleteName.contains("#")) {
            String[] names = testCaseCompleteName.split("#");
            this.testCaseClass = names[0];
            this.testCaseName = names[1];
            if (testCaseName.contains("[")) {
                this.isParameterized = true;
                int idxPar = testCaseName.indexOf("[");
                this.testCaseName = this.testCaseName.substring(0, idxPar);
            }
        }
    }

    public String getTestCaseCompleteName() {
        return testCaseCompleteName;
    }

    public void setTestCaseCompleteName(String testCaseCompleteName) {
        this.testCaseCompleteName = testCaseCompleteName;
    }

    public String getTestCaseName() {
        return testCaseName;
    }

    public void setTestCaseName(String testCaseName) {
        this.testCaseName = testCaseName;
    }

    public String getTestCaseClass() {
        return testCaseClass;
    }

    public void setTestCaseClass(String testCaseClass) {
        this.testCaseClass = testCaseClass;
    }

    public String getTrance() {
        return trance;
    }

    public void setTrance(String trance) {
        this.trance = trance;
    }

    public boolean isCorrect() {

        return this.correct;
    }

    @Override
    public String toString() {
        return "TestCaseResult [testCaseName=" + testCaseName + ", testCaseClass=" + testCaseClass + ", correct="
                + correct + "]";
    }
}
