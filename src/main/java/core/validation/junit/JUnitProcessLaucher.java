package core.validation.junit;

import conf.ConfigurationProperties;
import core.validation.entity.TestResult;
import org.apache.log4j.Logger;
import util.URLUtil;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Junit单测执行器启动器
 * 启动一个进程通过命令行调用JUnitSuiteExternalExecutor来执行单元测试并处理输出信息
 */
public class JUnitProcessLaucher {
    protected Logger log = Logger.getLogger(Thread.currentThread().getName());

    // 是否可以被中断
    boolean avoidInterruption = false;
    // 进程输出是否被重定向到文件
    boolean outputToFile = ConfigurationProperties.getPropertyBool(ConfigurationProperties.JUNIT_PROCESS_OUTPUT_TO_FILE);

    public JUnitProcessLaucher(boolean avoidInterruption) {
        super();
        this.avoidInterruption = avoidInterruption;
    }

    public JUnitProcessLaucher() {
        this(false);
    }

    /**
     * 启动一个进程执行单元测试
     * @param jvmPath
     * @param classpath
     * @param classesToExecute
     * @param waitTime
     * @return
     */
    public TestResult execute(String jvmPath, URL[] classpath, List<String> classesToExecute, int waitTime) {
        return execute(jvmPath, URLUtil.urlArrayToString(classpath), classesToExecute, waitTime);
    }

    /**
     * 启动一个进程，通过java命令行启动JUnitSuiteExternalExecutor类来运行单元测试
     * @param jvmPath
     * @param classpath
     * @param classesToExecute
     * @param waitTime
     * @return
     */
    public TestResult execute(String jvmPath, String classpath, List<String> classesToExecute, int waitTime) {
        jvmPath += File.separator + "java";
        List<String> classes = new ArrayList<>(new HashSet<>(classesToExecute));

        // 启动一个进程来执行单元测试命令
        Process p  = null;
        try {
            File ftemp = null;
            if (outputToFile) {
                ftemp = File.createTempFile("out", "txt");
            }

            // 构建java命令行
            List<String> command = new ArrayList<>();
            command.add("cmd.exe");
            command.add("/c");
            command.add(jvmPath);
            command.add("-Xmx2048m");
            command.add("-cp");
            command.add(classpath);
            command.add(laucherClassName().getCanonicalName());
            command.addAll(classes);

            // 打印命令
            printCommandToExecute(command, waitTime);

            // 创建进程
            ProcessBuilder pb = new ProcessBuilder();
            pb.command(command);

            if (outputToFile) {
                pb.redirectOutput(ftemp);
            } else {
                pb.redirectOutput();
            }
            pb.redirectErrorStream(true);
            pb.directory(new File(ConfigurationProperties.getProperty(ConfigurationProperties.LOCATION)));

            // 启动进程
            p = pb.start();

            if (!p.waitFor(waitTime, TimeUnit.MILLISECONDS)) {
                killProcess(p, waitTime);
                return null;
            }

            if (!avoidInterruption) {
                p.exitValue();
            }

            // 获取进程输出
            BufferedReader output = null;
            if (outputToFile) {
                output = new BufferedReader(new FileReader(ftemp.getAbsolutePath(), Charset.forName("GBK")));
            } else {
                output = new BufferedReader(new InputStreamReader(p.getInputStream()));
            }
            // 解析输出，获得单元测试执行结果
            TestResult result = getTestResult(output);

            // 结束进程
            p.destroyForcibly();

            return result;
        } catch (IOException | InterruptedException e) {
            log.info("The Process that runs JUnit test cases had problems: " + e.getMessage());
            killProcess(p, waitTime);
        }

        return null;
    }

    /**
     * 对junit执行器的输出进行分析，返回单元测试执行结果
     * @param in
     * @return
     */
    protected TestResult getTestResult(BufferedReader in) {
        log.debug("Analyzing output from process");

        TestResult testResult = new TestResult();
        boolean success = false;
        StringBuilder processOut = new StringBuilder();

        try {
            String line;
            while ((line = in.readLine()) != null) {
                processOut.append(line + "\n");

                if (line.startsWith(JUnitSuiteExternalExecutor.OUTPUT_SEPARATOR)) {
                    String[] resultPrinted = line.split(JUnitSuiteExternalExecutor.OUTPUT_SEPARATOR);

                    int runTestCount = Integer.valueOf(resultPrinted[1]);
                    testResult.casesExecuted = runTestCount;

                    int runFailTestCount = Integer.valueOf(resultPrinted[2]);
                    testResult.failures = runFailTestCount;

                    if (resultPrinted.length > 3 && !"".equals(resultPrinted[3])) {
                        String[] failingTestList = resultPrinted[3].replace("[", "").replace("]", "").split(",");
                        for (String failingTest : failingTestList) {
                            failingTest = failingTest.trim();
                            if (!failingTest.isEmpty() && !failingTest.equals("-"))
                                testResult.failTest.add(failingTest);
                        }
                    }

                    success = true;
                }
            }
            log.info(processOut.toString());
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (success) {
            return testResult;
        } else {
            log.error("Error reading the validation process, output: " + processOut);
            return null;
        }
    }

    public Class<?> laucherClassName() {
        return JUnitSuiteExternalExecutor.class;
    }

    private void killProcess(Process p, int waitTime) {
        if (p == null) {
            return;
        }

        p.destroyForcibly();
    }

    private void printCommandToExecute(List<String> command, int waitTime) {
        String commandString = toString(command);
        log.debug("Executing process: (timeout" + waitTime / 1000 + "secs) \n" + commandString);
    }

    private String toString(List<String> command) {
        String commandString = command.toString().replace("[", "").replace("]", "").replace(",", " ");
        return commandString;
    }

}
