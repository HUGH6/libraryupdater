package core.manipulation.bytecode.compiler.base;

import javax.tools.SimpleJavaFileObject;
import java.net.URI;

/**
 * 表示编译输入的源代码
 */
public class SourceCodeFileObject extends SimpleJavaFileObject {
    // 代码文本
    private String sourceContent;

    public SourceCodeFileObject(String simpleClassName, String sourceContent) {
        super(URI.create(simpleClassName + Kind.SOURCE.extension), Kind.SOURCE);
        this.sourceContent = sourceContent;
    }

    /**
     * 返回代码文本
     * @param ignoreEncodingErrors
     * @return
     */
    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return sourceContent;
    }


}
