package core.faultlocation.entity.runtestsuite;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TestSuiteProcessor {
    static final int CLASS_SUFFIX_LENGTH = ".class".length();
    static final int JAVA_SUFFIX_LENGTH = ".java".length();

    private final ClassFilter tester;
    private final ClassFinder finder;

    public TestSuiteProcessor(ClassFinder finder, ClassFilter tester) {
        this.tester = tester;
        this.finder = finder;
    }

    /**
     * 加载类
     * @return
     */
    public Class<?>[] process() {
        List<Class<?>> classes = new ArrayList<>();
        for (String fileName : finder.getClasses()) {
            String className;
            if (isJavaFile(fileName)) {
                className = classNameFromJava(fileName);
            } else if (isClassFile(fileName)) {
                className = classNameFromFile(fileName);
            } else {
                continue;
            }

            if (!tester.acceptInnerClass() && isInnerClass(className)) {
                continue;
            }

            if (!className.contains("$")) {
                try {
                    Class<?> clazz = Class.forName(className);

                    if (clazz.isLocalClass() || clazz.isAnonymousClass()) {
                        continue;
                    }

                    if (tester.acceptClass(clazz)) {
                        classes.add(clazz);
                    }
                } catch (ClassNotFoundException e) {
                    try {
                        ClassLoader tmp = Thread.currentThread().getContextClassLoader();
                        Class<?> clazz = Class.forName(className, false, tmp);
                        if (clazz.isLocalClass() || clazz.isAnonymousClass()) {
                            continue;
                        }
                        if (tester.acceptClass(clazz)) {
                            classes.add(clazz);
                        }
                    } catch (ClassNotFoundException cnfe2) {
                        cnfe2.printStackTrace();
                    } catch (NoClassDefFoundError ncdfe) {
                        // ignore
                    }
                } catch (NoClassDefFoundError ncdfe) {
                    // ignore
                }
            }
        }

        Collections.sort(classes, new Comparator<Class<?>>() {
            @Override
            public int compare(Class<?> o1, Class<?> o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        return classes.toArray(new Class[0]);
    }

    private boolean isJavaFile(String fileName) {
        return fileName.endsWith(".java");
    }

    private boolean isInnerClass(String className) {
        return className.contains("$");
    }

    private boolean isClassFile(String classFileName) {
        return classFileName.endsWith(".class");
    }

    /**
     * 从Java源文件中获取类名
     * @param fileName
     * @return
     */
    private String classNameFromJava(String fileName) {
        String s = replaceFileSeparators(cutOffExtension(fileName, JAVA_SUFFIX_LENGTH));
        while (s.startsWith(".")) {
            s = s.substring(1);
        }

        return s;
    }

    /**
     * 从类文件名获取类名
     * @param classFileName
     * @return
     */
    private String classNameFromFile(String classFileName) {
        String s = replaceFileSeparators(cutOffExtension(classFileName, CLASS_SUFFIX_LENGTH));
        while (s.startsWith(".")) {
            s = s.substring(1);
        }

        return s;
    }

    /**
     * 移除扩展后缀
     * @param classFileName
     * @param length
     * @return
     */
    private String cutOffExtension(String classFileName, int length) {
        return classFileName.substring(0, classFileName.length() - length);
    }

    /**
     * 将路径中的文件分割符替换为点.
     * @param path
     * @return
     */
    private String replaceFileSeparators(String path) {
        String result = path.replace(File.separatorChar, '.');
        if (File.separatorChar != '/') {
            result = result.replace('/', '.');
        }
        return result;
    }
}
