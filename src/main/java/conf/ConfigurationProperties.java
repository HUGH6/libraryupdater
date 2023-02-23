package conf;

import java.io.InputStream;
import java.util.Properties;

/**
 * 用于存储系统配置参数
 */
public class ConfigurationProperties {
    public static final Properties properties = new Properties();
    public static final String defaultPropertyFile = "conf.properties";

    /************************************************
     * 配置参数名称常量
     ***********************************************/
    // 项目路径
    public static final String LOCATION = "location";
    // maven命令路径配置
    public static final String MAVEN_DIR = "mvn_dir";
    // 项目依赖的第三方jar包路径
    public static final String DEPENDENCIES_PATH = "dependencies_path";
    // 项目标识符
    public static final String PROJECT_NAME = "project_name";
    // 源api
    public static final String ORIGIN_API = "origin_api";
    // 目标api
    public static final String TARGET_API = "target_api";
    // 迁移工作目录根路径
    public static final String WORKING_DIR_ROOT = "working_dir_root";
    // 存放源代码的迁移工作目录
    public static final String WORKING_DIR_SOURCE_CODE = "working_dir_source_code";
    // 存放字节码的迁移工作目录
    public static final String WORKING_DIR_BYTECODE = "working_dir_bytecode";
    // 存放项目原始代码的目录
    public static final String ORIGINAL_DIR_SRC = "original_dir_src";
    // 存放项目原始字节码的目录
    public static final String ORIGINAL_DIR_BIN = "original_dir_bin";
    // 存放项目原始测试代码的目录
    public static final String ORIGINAL_DIR_TEST = "original_dir_test";
    // 存放项目原始测试代码字节码的目录
    public static final String ORIGINAL_DIR_TEST_BIN = "original_dir_test_bin";
    // 存放项目其他数据的目录
    public static final String ORIGIN_DIR_DATA = "origin_dir_data";

    // 最大迁移时间
    public static final String MAX_TIME = "max_time";
    // 最大迭代次数
    public static final String MAX_GENERATION = "max_generation";
    // 单测执行最大时间
    public static final String MAX_TEST_SUITE_TIME = "max_test_suite_time";


    // 用于执行单元测试的jvm路径
    public static final String JVM_FOR_TEST_EXECUTION = "jvm_for_test_execution";
    // 单元测试结果是否输出到文件
    public static final String JUNIT_PROCESS_OUTPUT_TO_FILE = "junit_process_output_to_file";

    // 故障定位类型
    public static final String FAULT_LOCALIZATION_TYPE = "fault_localization_type";
    // 故障定位可疑度阈值
    public static final String FAULT_LOCALIZATION_THRESHOLD = "fault_localization_threshold";
    // gzoltar版本号
    public static final String GZOLTAR_VERSION = "gzoltar_version";
    // gzoltar jar路径
    public static final String GZOLTAR_JAR_LOCATION = "gzoltar_jar_location";
    // 指定进行故障定位的包
    public static final String PACKAGE_TO_INSTRUMENT = "package_to_instrument";


    // 程序验证器类型
    public static final String VALIDATION_TYPE = "validation_type";

    // 迁移引擎类型
    public static final String MIGRATION_MODE = "migration_mode";



    // java版本
    public static final String COMPILATION_LEVEL = "compilation_level";

    // 种群大小
    public static final String POPULATION_SIZE = "population_size";

    // 源版本软件库路径
    public static final String SOURCE_LIBRARY_PATH = "source_library_path";
    // 目标版本软件库路径
    public static final String TARGET_LIBRARY_PATH = "target_library_path";

    static {
        loadPropertiesFromFile();
    }

    protected static void loadPropertiesFromFile() {
        try (InputStream propFile =
                     ConfigurationProperties.class.getClassLoader().getResourceAsStream(defaultPropertyFile)){
            properties.load(propFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean hasProperty(String key) {
        return properties.getProperty(key) != null;
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    /**
     * 以Integer类型返回属性值，若熟悉不存在，会返回0
     * @param key 熟悉名称
     * @return Integer类型的返回值，若熟悉key不存在，则返回0
     */
    public static Integer getPropertyInt(String key) {
        if (properties.getProperty(key) == null) {
            return 0;
        }
        return Integer.valueOf(properties.getProperty(key));
    }

    public static Boolean getPropertyBool(String key) {
        return Boolean.valueOf(properties.getProperty(key));
    }

    public static Double getPropertyDouble(String key) {
        return Double.valueOf(properties.getProperty(key));
    }

    /**
     * Clean/remove all properties, then reload the default properties
     */
    public static void reload() {
        properties.clear();
        loadPropertiesFromFile();
    }

    /**
     * 用于打印配置属性
     * @return 配置属性
     */
    public static String printConfProperties() {
        StringBuilder conf = new StringBuilder();
        properties.forEach((key, value) -> {
            conf.append(key);
            conf.append(": ");
            conf.append(value);
            conf.append("\n");
        });
        return conf.toString();
    }
}
