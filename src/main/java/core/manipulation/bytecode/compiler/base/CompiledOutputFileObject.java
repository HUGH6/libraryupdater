package core.manipulation.bytecode.compiler.base;

import javax.tools.SimpleJavaFileObject;
import java.io.*;
import java.net.URI;

/**
 * 表示编译后的输出
 */
public class CompiledOutputFileObject extends SimpleJavaFileObject {
    // 编译结果回调的OutputStream
    // 回调成功后就能通过getByteCode()方法获取目标类编译后的字节码字节数组
    protected ByteArrayOutputStream byteCodes;

    public CompiledOutputFileObject(String qualifiedName, Kind kind) {
        super(URI.create(qualifiedName), kind);
    }

    public CompiledOutputFileObject(String qualifiedName, Kind kind, byte[] bytes) {
        this(qualifiedName, kind);
        setBytecodes(bytes);
    }

    @Override
    public InputStream openInputStream() {
        return new ByteArrayInputStream(byteCodes());
    }

    /**
     * 注意这个方法是编译结果回调的OutputStream
     * 回调成功后就能通过getByteCode()方法获取目标类编译后的字节码字节数组
     * @return
     */
    @Override
    public OutputStream openOutputStream() {
        this.byteCodes = new ByteArrayOutputStream();
        return byteCodes;
    }

    /**
     * 写入字节数组到编译结果中
     * @param bytes
     */
    private void setBytecodes(byte[] bytes) {
        try {
            openOutputStream().write(bytes);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    /**
     * 返回编译后的字节码字节数组
     * @return
     */
    public byte[] byteCodes() {
        return byteCodes.toByteArray();
    }
}
