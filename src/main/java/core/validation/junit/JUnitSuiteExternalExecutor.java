package core.validation.junit;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runners.model.InitializationError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 该类用于运行一个junit测试套件（一组测试用例）
 * 对程序变体进行验证会通过外部进程的方式用命令行调用该类实现单测执行
 */
public class JUnitSuiteExternalExecutor {
    // 执行成功的测试用例
    List<String> successTestCases = new ArrayList<String>();
    // 执行失败的测试用例
    List<String> failTestCases = new ArrayList<String>();

    // 输出信息分割符号
    public final static String OUTPUT_SEPARATOR = "##";

    /**
     * 运行junit单元测试
     * @param classesName
     * @return
     * @throws Exception
     */
    public Result run(String[] classesName) throws Exception {
        // 获取需要运行的类
        List<Class<?>> classes = getClassesToRun(classesName);
        JUnitCore runner = new JUnitCore();
        return runner.run(classes.toArray(new Class[0]));
    }

    /**
     * 根据要运行的类名称获取类对象
     * @param classesName
     * @return
     * @throws ClassNotFoundException
     */
    protected List<Class<?>> getClassesToRun(String[] classesName) throws ClassNotFoundException {
        return Arrays.stream(classesName)
            .map((clazzName) -> {
                Class<?> clazz = null;
                try {
                    clazz = Class.forName(clazzName);
                    return clazz;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    return null;
                }
            })
            .filter(clazz -> clazz != null)
            .distinct()
            .collect(Collectors.toList());
    }

    public String getFailureOutput(Result result) {
        String out = "[";
        int count = 0;
        int failures = 0;
        try {
            for (Failure f : result.getFailures()) {
                String s = failureMessage(f);
                if (!s.startsWith("warning")) {
                    failures++;
                }
                out += s + "-,";
                count++;
                if (count > 10) {
                    out += "...and " + (result.getFailureCount() - 10) + " failures more,";
                    // break;
                }
            }
        } catch (Exception e) {
            // We do not care about this exception,
        }
        out = out + "]";
        return (OUTPUT_SEPARATOR + result.getRunCount() + OUTPUT_SEPARATOR + failures + OUTPUT_SEPARATOR + out + OUTPUT_SEPARATOR);
    }

    protected String failureMessage(Failure f) {
        try {
            return f.toString();
        } catch (Exception e) {
            return f.getTestHeader();
        }
    }

    /**
     * 测试main函数
     * @param args
     */
    public static void main(String[] args) throws Exception, InitializationError {
        JUnitSuiteExternalExecutor executor = new JUnitSuiteExternalExecutor();
        Result result = executor.run(args);
        System.out.println(executor.getFailureOutput(result));
        System.exit(0);
    }
}
