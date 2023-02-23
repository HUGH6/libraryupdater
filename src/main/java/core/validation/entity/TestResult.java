package core.validation.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 记录一次单元测试运行结果
 */
public class TestResult {
    // 执行的测试数量
    public int casesExecuted = 0;
    // 失败的数量
    public int failures = 0;
    // 成功的测试类
    public List<String> successTest = new ArrayList<>();
    // 失败的测试类
    public List<String> failTest = new ArrayList<>();

    public List<String> getSuccessTest() {
        return successTest;
    }

    public void setSuccessTest(List<String> successTest) {
        this.successTest = successTest;
    }

    public List<String> getFailTest() {
        return failTest;
    }

    public void setFailTest(List<String> failTest) {
        this.failTest = failTest;
    }

    public boolean isSuccessful(){
        return failures == 0;
    }

    @Override
    public String toString() {
        return "TR: Success: "+ (failures == 0) + ", failTest= "
                + failures + ", is successful: "+this.isSuccessful()+", cases executed: "+casesExecuted+"] ,"+ this.failTest;
    }

    public int getFailureCount(){
        return failures;
    }

    public int getCasesExecuted() {
        return casesExecuted;
    }
}
