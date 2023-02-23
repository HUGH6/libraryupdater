package conf;

public enum ProjectConfigurationEnum {
    WORKING_DIR_ROOT,       // 迁移的根工作目录
    WORKING_DIR_SOURCE_CODE,// 存放源代码的迁移工作目录
    WORKING_DIR_BYTECODE,   // 存放字节码的迁移工作目录
    ORIGINAL_DIR_PROJECT_ROOT,  // 待迁移项目根目录
    ORIGINAL_DIR_SRC,       // 存放项目原始代码的目录
    ORIGINAL_DIR_BIN,       // 存放项目原始字节码的目录
    ORIGINAL_DIR_TEST_SRC,  // 存放项目测试代码源码的目录
    ORIGINAL_DIR_TEST_BIN,  // 存放项目原始测试代码字节码的目录
    DEPENDENCIES,           // 存放项目依赖的目录
    ORIGIN_DIR_DATA,        // 存放项目其他数据的目录
    REGRESSION_TEST_CASES,  // 验证代码的单元测试
    FAILING_TEST_CASES,     // 执行失败的单元测试
}
