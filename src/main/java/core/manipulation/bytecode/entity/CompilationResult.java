package core.manipulation.bytecode.entity;

import java.util.List;
import java.util.Map;

/**
 * 表示编译结果
 */
public class CompilationResult {
    // 编译的字节码
    private Map<String, byte[]> byteCodes;
    // 编译错误信息
    private List<String> errorList = null;

    public CompilationResult(Map<String, byte[]> byteCodes,List<String> errorList) {
        this.byteCodes = byteCodes;
        this.errorList = errorList;
    }

    public Map<String, byte[]> getByteCodes() {
        return byteCodes;
    }

    public void setByteCodes(Map<String, byte[]> byteCodes) {
        this.byteCodes = byteCodes;
    }

    /**
     * 是否编译成功没有产生编译错误
     * @return
     */
    public boolean compiles() {
        return errorList == null || errorList.isEmpty();
    }

    public List<String> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<String> errorList) {
        this.errorList = errorList;
    }

    @Override
    public String toString() {
        return "CompilationResult: byteCodes=" + byteCodes.size()+" errors (" + errorList.size()+ ") "+errorList + "]";
    }
}
