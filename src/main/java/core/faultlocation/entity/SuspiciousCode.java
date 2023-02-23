package core.faultlocation.entity;

import core.entity.SuspiciousModificationPoint;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

/**
 * 表示一个类中可疑（可能有bug）的代码行
 */
public class SuspiciousCode {
    // 类名
    private String className;
    // 方法名
    private String methodName;
    // 代码行数
    private int lineNumber;
    // 改行的可疑度
    private double suspiciousValue;
    // 文件名
    private String fileName;
    // 测试覆盖频率，key是测试的标识id，value是被该测试执行的次数
    private Map<Integer, Integer> coverage = null;
    // 测试结果
    protected List<TestCaseResult> coveredByTests = null;
    // 浮点数输出格式
    DecimalFormat format = new DecimalFormat("#.###");

    public SuspiciousCode() {}

    public SuspiciousCode(String className, String methodName, int lineNumber,
                          double suspiciousValue, Map<Integer, Integer> frequency) {
        super();
        this.className = className;
        this.methodName = methodName;
        this.lineNumber = lineNumber;
        this.suspiciousValue = suspiciousValue;
        this.coverage = frequency;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public double getSuspiciousValue() {
        return suspiciousValue;
    }

    public String getSuspiciousValueString() {
        return this.format.format(this.suspiciousValue);
    }

    public void setSuspiciousValue(double suspiciousValue) {
        this.suspiciousValue = suspiciousValue;
    }

    public String getClassName() {
        // 对于内部类，会以顶级类$内部类的形式命名，提取顶级类名
        int i = className.indexOf("$");
        if (i != -1) {
            return className.substring(0, i);
        }

        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Map<Integer, Integer> getCoverage() {
        return coverage;
    }

    public void setCoverage(Map<Integer, Integer> coverage) {
        this.coverage = coverage;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<TestCaseResult> getCoveredByTests() {
        return coveredByTests;
    }

    public void setCoveredByTests(List<TestCaseResult> coveredByTests) {
        this.coveredByTests = coveredByTests;
    }

    @Override
    public String toString() {
        return "Candidate [className=" + className + ", methodName=" + methodName + ", lineNumber=" + lineNumber
                + ", susp=" + suspiciousValue + "]";
    }
}
