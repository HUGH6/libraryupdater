package core.manipulation.bytecode.compiler.base;

import javax.tools.*;
import javax.tools.JavaFileObject.Kind;
import java.io.IOException;
import java.net.URI;
import java.util.*;

/**
 * 内存文件管理器
 * 1.在编译过程中，首先编译器会遍历JavaFileManager对象，获取指定位置的所有符合要求的JavaFileObject对象，
 *   甚至可以递归遍历，这时调用的是list()方法，该方法会扫描所有涉及的到的包，包括一个类和它实现的接口和继承的类
 * 2.根据获取到的JavaFileObject对象，获取它的二进制表示的名称，通过调用inferBinaryName()方法
 * 3.之后输出编译类，类表示为JavaFileObject对象，注意此时的JavaFileObject.Kind为CLASS，
 *   调用的方法是getJavaFileForOutput()，注意该方法的调用是在JavaFileObject中openOutputStream()方法之前
 */
public class VirtualFileObjectManager extends ForwardingJavaFileManager<JavaFileManager> {
    // 编译的源码
    private Map<URI, SourceCodeFileObject> sourceFiles;
    // 编译后的字节码
    private Map<String, CompiledOutputFileObject> classFiles;

    public VirtualFileObjectManager(JavaFileManager fileManager) {
        super(fileManager);
        classFiles = new HashMap<>();
        sourceFiles = new HashMap<>();
    }

    @Override
    public FileObject getFileForInput(Location location, String packageName, String relativeName) throws IOException {
        URI fileURI = uriFor(location, packageName, relativeName);
        if (containsSourceFile(fileURI)) {
            return getSourceFile(fileURI);
        }

        return super.getFileForInput(location, packageName, relativeName);
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String qualifiedName, Kind kind, FileObject outputFile) throws IOException {
        CompiledOutputFileObject classFile = new CompiledOutputFileObject(qualifiedName, kind);
        this.classFiles.put(qualifiedName, classFile);
        return classFile;
    }

    @Override
    public String inferBinaryName(Location location, JavaFileObject file) {
        if (SourceCodeFileObject.class.isInstance(file) || CompiledOutputFileObject.class.isInstance(file)) {
            return file.getName();
        }
        return super.inferBinaryName(location, file);
    }

    @Override
    public Iterable<JavaFileObject> list(Location location, String packageName, Set<Kind> kinds, boolean recurse) throws IOException {
        Iterable<JavaFileObject> result = super.list(location, packageName, kinds, recurse);
        List<JavaFileObject> files = new ArrayList<>();//MetaList.newLinkedList();
        if (location == StandardLocation.CLASS_PATH && kinds.contains(JavaFileObject.Kind.CLASS)) {
            for (JavaFileObject file : this.sourceFiles.values()) {
                if (file.getKind() == Kind.CLASS && file.getName().startsWith(packageName)) {
                    files.add(file);
                }
            }
            files.addAll(this.classFiles.values());
        } else if (location == StandardLocation.SOURCE_PATH && kinds.contains(JavaFileObject.Kind.SOURCE)) {
            for (JavaFileObject file : this.sourceFiles.values()) {
                if (file.getKind() == Kind.SOURCE && file.getName().startsWith(packageName)) {
                    files.add(file);
                }
            }
        }

        this.addAll(files,result);
        return files;
    }

    public void addSourceFile(Location location, String packageName, String simpleClassName, SourceCodeFileObject sourceFile) {
        URI fileURI = uriFor(location, packageName, simpleClassName);
        this.sourceFiles.put(fileURI, sourceFile);
    }

    public void addCompiledClasses(Map<String, byte[]> compiledClasses) {
        for (String qualifedName: compiledClasses.keySet()) {
            this.classFiles.put(qualifedName, new CompiledOutputFileObject(qualifedName, JavaFileObject.Kind.CLASS, compiledClasses.get(qualifedName)));
        }
    }

    public static <T> boolean addAll(Collection<T> destination, Iterable<? extends T> elements) {
        boolean changed = false;
        for (T element : elements) {
            changed |= destination.add(element);
        }
        return changed;
    }

    private URI uriFor(Location location, String packageName, String simpleClassName) {
        String urlScheme = location.getName() + '/' + packageName + '/' + simpleClassName + ".java";
        return URI.create(urlScheme);
    }

    public Map<String, CompiledOutputFileObject> getClassFiles() {
        return this.classFiles;
    }

    public Map<URI, SourceCodeFileObject> getSourceFiles() {
        return this.sourceFiles;
    }

    public CompiledOutputFileObject getClassFile(String qualifiedName) {
        return getClassFiles().get(qualifiedName);
    }

    public SourceCodeFileObject getSourceFile(URI fileURI) {
        return getSourceFiles().get(fileURI);
    }

    public boolean containsClassFile(String qualifiedName) {
        return getClassFiles().containsKey(qualifiedName);
    }

    public boolean containsSourceFile(URI fileURI) {
        return getSourceFiles().containsKey(fileURI);
    }

    public int numberOfClassFiles() {
        return getClassFiles().size();
    }

    public int numberOfSourceFiles() {
        return getSourceFiles().size();
    }
}
