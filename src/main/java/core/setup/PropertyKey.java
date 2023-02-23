package core.setup;

/**
 * 表示配置项的常量值
 */
public class PropertyKey {
    public static final String JavaComplianceLevel = "javacompliancelevel"; // 编译器级别
    public static final String KeepComments        = "keepcomments";        // 构建spoon模型时是否保留注释
    public static final String NoClasspathSpoon    = "noclasspathspoon";    // 构建spoon模型时是否设置了classpath
    public static final String PreserveLineNumber  = "preservelinenumbers"; // 构建spoon模型时是否保留行号
    public static final String ParseSourceFromOriginal = "parsesourcefromoriginal";

    /******************************
     * 用于提取软件库的配置
     ******************************/
    public static final String LibraryIdentifier    = "libraryIdentifier";  // 项目标识
    public static final String LibraryRoot          = "libraryRoot";        // 项目根目录
    public static final String LibrarySrcPath       = "librarySrcPath";     // 项目源码路径
    public static final String LibraryDependencyPath= "libraryDependencyPath";  // 项目依赖目录



    /******************************
     * 单元测试配置
     ******************************/
    public static final String JUnitProcessOutputToFile = "junit_process_output_to_file";   // junit单测进程输出是否重定向到文件
    public static final String Location = "location";   // working directory for Gzoltar
    public static final String ForceExecuteRegression = "force_execute_regression";         // 执行回归测试
    public static final String VariantFolderPrefixName = "variant_folder_prefix_name";      // 程序变体的文件夹前缀名称
    public static final String Jvm4TestExecution = "jvm_4_test_execution";                  // 用于执行测试的jvm路径
    public static final String TestSuiteMaxTime = "test_suite_max_time";                               // 执行单元测试套件的时间
    public static final String TestFailingCaseMaxTime = "test_failing_case_max_time";       // 执行失败测试用例的时间
    public static final String TestByStep = "test_by_step";                                 // 逐个执行测试用例
    public static final String Validation = "validation";                                   // 执行单元测试验证程序变体的方式，（1）process：启动一个进程执行单元测试

}
