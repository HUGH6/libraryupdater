package core.validation.junit;

import core.validation.entity.TestResult;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestJUnitProcessLaucher {
    private JUnitProcessLaucher laucher = null;

    @Before
    public void init() {
        this.laucher = new JUnitProcessLaucher();
    }

    @Test
    public void testExecute1() {
        String jvmPath = "D:\\programs\\Java\\jdk-11.0.14\\bin";
        String classpath = "\"D:\\programs\\JetBrains\\IntelliJ IDEA Community Edition 2021.3.2\\lib\\idea_rt.jar;D:\\programs\\JetBrains\\IntelliJ IDEA Community Edition 2021.3.2\\plugins\\junit\\lib\\junit5-rt.jar;D:\\programs\\JetBrains\\IntelliJ IDEA Community Edition 2021.3.2\\plugins\\junit\\lib\\junit-rt.jar;E:\\projects\\libraryupdater\\target\\test-classes;E:\\projects\\libraryupdater\\target\\classes;E:\\mvn\\fr\\inria\\gforge\\spoon\\spoon-core\\9.2.0-beta-1\\spoon-core-9.2.0-beta-1.jar;E:\\mvn\\org\\eclipse\\jdt\\org.eclipse.jdt.core\\3.26.0\\org.eclipse.jdt.core-3.26.0.jar;E:\\mvn\\com\\martiansoftware\\jsap\\2.1\\jsap-2.1.jar;E:\\mvn\\org\\slf4j\\slf4j-api\\1.7.32\\slf4j-api-1.7.32.jar;E:\\mvn\\commons-io\\commons-io\\2.11.0\\commons-io-2.11.0.jar;E:\\mvn\\org\\apache\\maven\\maven-model\\3.8.2\\maven-model-3.8.2.jar;E:\\mvn\\org\\codehaus\\plexus\\plexus-utils\\3.2.1\\plexus-utils-3.2.1.jar;E:\\mvn\\org\\apache\\commons\\commons-lang3\\3.12.0\\commons-lang3-3.12.0.jar;E:\\mvn\\com\\fasterxml\\jackson\\core\\jackson-databind\\2.12.4\\jackson-databind-2.12.4.jar;E:\\mvn\\com\\fasterxml\\jackson\\core\\jackson-annotations\\2.12.4\\jackson-annotations-2.12.4.jar;E:\\mvn\\com\\fasterxml\\jackson\\core\\jackson-core\\2.12.4\\jackson-core-2.12.4.jar;E:\\mvn\\org\\apache\\commons\\commons-compress\\1.21\\commons-compress-1.21.jar;E:\\mvn\\org\\apache\\maven\\shared\\maven-invoker\\3.1.0\\maven-invoker-3.1.0.jar;E:\\mvn\\org\\apache\\maven\\shared\\maven-shared-utils\\3.3.3\\maven-shared-utils-3.3.3.jar;E:\\mvn\\javax\\inject\\javax.inject\\1\\javax.inject-1.jar;E:\\mvn\\com\\github\\javaparser\\javaparser-core\\3.24.2\\javaparser-core-3.24.2.jar;E:\\mvn\\com\\github\\javaparser\\javaparser-symbol-solver-core\\3.24.2\\javaparser-symbol-solver-core-3.24.2.jar;E:\\mvn\\org\\javassist\\javassist\\3.28.0-GA\\javassist-3.28.0-GA.jar;E:\\mvn\\com\\google\\guava\\guava\\31.1-jre\\guava-31.1-jre.jar;E:\\mvn\\com\\google\\guava\\failureaccess\\1.0.1\\failureaccess-1.0.1.jar;E:\\mvn\\com\\google\\guava\\listenablefuture\\9999.0-empty-to-avoid-conflict-with-guava\\listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar;E:\\mvn\\com\\google\\code\\findbugs\\jsr305\\3.0.2\\jsr305-3.0.2.jar;E:\\mvn\\org\\checkerframework\\checker-qual\\3.12.0\\checker-qual-3.12.0.jar;E:\\mvn\\com\\google\\errorprone\\error_prone_annotations\\2.11.0\\error_prone_annotations-2.11.0.jar;E:\\mvn\\com\\google\\j2objc\\j2objc-annotations\\1.3\\j2objc-annotations-1.3.jar;E:\\mvn\\org\\mongodb\\mongo-java-driver\\3.11.2\\mongo-java-driver-3.11.2.jar;E:\\mvn\\junit\\junit\\4.13.1\\junit-4.13.1.jar;E:\\mvn\\org\\hamcrest\\hamcrest-core\\1.3\\hamcrest-core-1.3.jar;E:\\mvn\\org\\junit\\jupiter\\junit-jupiter-engine\\5.3.2\\junit-jupiter-engine-5.3.2.jar;E:\\mvn\\org\\apiguardian\\apiguardian-api\\1.0.0\\apiguardian-api-1.0.0.jar;E:\\mvn\\org\\junit\\platform\\junit-platform-engine\\1.3.2\\junit-platform-engine-1.3.2.jar;E:\\mvn\\org\\junit\\jupiter\\junit-jupiter-api\\5.3.2\\junit-jupiter-api-5.3.2.jar;E:\\mvn\\org\\opentest4j\\opentest4j\\1.1.1\\opentest4j-1.1.1.jar;E:\\mvn\\org\\junit\\platform\\junit-platform-commons\\1.3.2\\junit-platform-commons-1.3.2.jar;E:\\mvn\\org\\apache\\logging\\log4j\\log4j-1.2-api\\2.13.3\\log4j-1.2-api-2.13.3.jar;E:\\mvn\\org\\apache\\logging\\log4j\\log4j-api\\2.13.3\\log4j-api-2.13.3.jar;E:\\mvn\\org\\apache\\logging\\log4j\\log4j-core\\2.13.3\\log4j-core-2.13.3.jar\"";
        List<String> classToExecute = new ArrayList<>();
        classToExecute.add("core.ingredient.util.TestStringDistance");
        int waitTime = 10000;

        TestResult res = laucher.execute(jvmPath, classpath, classToExecute, waitTime);

        System.out.println(res.isSuccessful());
        System.out.println(res.getCasesExecuted());
        System.out.println(res.getSuccessTest());
        System.out.println(res.getFailTest());
        System.out.println(res.getFailureCount());
    }
}
