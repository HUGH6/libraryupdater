package conf;

import org.apache.log4j.Logger;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于存储进行迁移的某个具体项目的项目信息，并体统一些常用信息的访问接口
 */
public class ProjectConfiguration {
    private static Logger logger = Logger.getLogger(ProjectConfiguration.class.getSimpleName());

    // 包含待迁移项目的所有属性项，以ProjectConfigurationEnum中定义的属性为key
    private Map<ProjectConfigurationEnum, Object> projectProperties = new HashMap<>();

    public ProjectConfiguration() {
        this.projectProperties.put(ProjectConfigurationEnum.ORIGINAL_DIR_SRC, new ArrayList<String>());
        this.projectProperties.put(ProjectConfigurationEnum.DEPENDENCIES, new ArrayList<String>());
    }

    public void setProperty(ProjectConfigurationEnum key, Object value) {
        this.projectProperties.put(key, value);
    }

    public Object getProperty(ProjectConfigurationEnum key) {
        return this.projectProperties.get(key);
    }

    /**
     * 返回String类型的属性值
     * @param key
     * @return
     */
    public String getStringProperty(ProjectConfigurationEnum key) {
        return (String) this.projectProperties.get(key);
    }

    /*******************************************************************
     * 以下方法是对特定项目属性的访问的封装
     *******************************************************************/

    /**
     * 迁移工作目录根路径
     * @return
     */
    public String getWorkingDirRoot() {
        return (String) this.projectProperties.get(ProjectConfigurationEnum.WORKING_DIR_ROOT);
    }

    public void setWorkingDirRoot(String rootDir) {
        this.projectProperties.put(ProjectConfigurationEnum.WORKING_DIR_ROOT, rootDir);
    }

    /**
     * 存放源码的迁移工作目录
     * @return
     */
    public String getWorkingDirForSourceCode() {
        return (String) this.projectProperties.get(ProjectConfigurationEnum.WORKING_DIR_SOURCE_CODE);
    }

    public void setWorkingDirForSourceCode(String inDir) {
        this.projectProperties.put(ProjectConfigurationEnum.WORKING_DIR_SOURCE_CODE, inDir);
    }

    /**
     * 存放字节码的迁移工作目录
     * @return
     */
    public String getWorkingDirForBytecode() {
        return (String) this.projectProperties.get(ProjectConfigurationEnum.WORKING_DIR_BYTECODE);
    }

    public void setWorkingDirForBytecode(String outDir) {
        this.projectProperties.put(ProjectConfigurationEnum.WORKING_DIR_BYTECODE, outDir);
    }

    /**
     * 设置原始代码目录路径
     * @param dirs
     */
    public void setOriginalDirSrc(List<String> dirs) {
        for (String dir : dirs) {
            addOriginalDirSrc(dir);
        }
    }

    /**
     * 添加原始代码目录路径
     * @param originalDir
     */
    public void addOriginalDirSrc(String originalDir) {
        ((List<String>) this.projectProperties.get(ProjectConfigurationEnum.ORIGINAL_DIR_SRC)).add(originalDir);
    }

    /**
     * 获取待迁移项目的源代码目录
     * @return
     */
    public List<String> getOriginalDirSrc() {
        return (List<String>) this.projectProperties.get(ProjectConfigurationEnum.ORIGINAL_DIR_SRC);
    }

    /**
     * 获取待迁移项目的编译字节码目录
     * @return
     */
    public List<String> getOriginalDirBin() {
        return (List<String>) this.projectProperties.get(ProjectConfigurationEnum.ORIGINAL_DIR_BIN);
    }

    public void setOriginalDirBin(List<String> originalDirBin) {
        this.projectProperties.put(ProjectConfigurationEnum.ORIGINAL_DIR_BIN, originalDirBin);
    }

    /**
     * 获取待迁移项目的测试代码源码目录
     * @return
     */
    public List<String> getOriginalDirTestSrc() {
        return (List<String>) this.projectProperties.get(ProjectConfigurationEnum.ORIGINAL_DIR_TEST_SRC);
    }

    public void setOriginalDirTestSrc(List<String> originalDirTestSrc) {
        this.projectProperties.put(ProjectConfigurationEnum.ORIGINAL_DIR_TEST_SRC, originalDirTestSrc);
    }

    /**
     * 获取待迁移项目的测试代码编译字节码目录
     * @return
     */
    public List<String> getOriginalDirTestBin() {
        return (List<String>) this.projectProperties.get(ProjectConfigurationEnum.ORIGINAL_DIR_TEST_BIN);
    }

    public void setOriginalDirTestBin(List<String> originalDirTestBin) {
        this.projectProperties.put(ProjectConfigurationEnum.ORIGINAL_DIR_TEST_BIN, originalDirTestBin);
    }

    /**
     * 设置待迁移项目依赖包路径列表
     * @param dependencies 依赖包路径列表
     */
    public void setDependencies(List<URL> dependencies) {
        this.projectProperties.put(ProjectConfigurationEnum.DEPENDENCIES, dependencies);
    }

    public void setDependencies(String dependencies) {
        String [] deps = dependencies.split(File.pathSeparator);
        for (String d : deps) {
            this.addLocationToClasspath(d);
        }
    }

    /**
     * 返回待迁移项目依赖jar包的路径列表
     * @return 依赖包路径列表
     */
    public List<URL> getDependencies() {
        return (List<URL>) this.projectProperties.get(ProjectConfigurationEnum.DEPENDENCIES);
    }

    /**
     * 返回待迁移项目依赖jar包路径列表的拼接字符串
     * @return 依赖包路径字符串
     */
    public String getDependenciesString() {
        List<URL> dependencies = (List<URL>) this.projectProperties.get(ProjectConfigurationEnum.DEPENDENCIES);
        StringBuilder deptStr = new StringBuilder();

        for (int idx = 0; idx < dependencies.size(); idx++) {
            URL url = dependencies.get(idx);
            deptStr.append(url.getPath());
            if (idx != dependencies.size() - 1) {
                deptStr.append(File.pathSeparator);
            }
        }
        return deptStr.toString();
    }

    /**
     * 返回待千迁移项目中其他目录数据的目录路径
     * @return
     */
    public String getOriginalDirData() {
        return (String) this.projectProperties.get(ProjectConfigurationEnum.ORIGIN_DIR_DATA);
    }

    public void setOriginalDirData(String dataFolder) {
        this.projectProperties.put(ProjectConfigurationEnum.ORIGIN_DIR_DATA, dataFolder);
    }

    /**
     * 获取项目根目录
     * @return
     */
    public String getOriginalDirProjectRoot() {
        return (String) this.projectProperties.get(ProjectConfigurationEnum.ORIGINAL_DIR_PROJECT_ROOT);
    }

    public void setOriginalDirProjectRoot(String root) {
        this.projectProperties.put(ProjectConfigurationEnum.ORIGINAL_DIR_PROJECT_ROOT, root);
    }

    /**
     * 获取当前测试项目的单元测试
     * @return
     */
    public List<String> getRegressionTestCases() {
        return (List<String>) this.projectProperties.get(ProjectConfigurationEnum.REGRESSION_TEST_CASES);
    }

    public void setRegressionTestCases(List<String> testCases) {
        this.projectProperties.put(ProjectConfigurationEnum.REGRESSION_TEST_CASES, testCases);
    }

    /**
     * 将指定路径添加到项目依赖列表中，如果是一个目录，则添加该目录下所有的jar文件
     * @param path
     */
    public void addLocationToClasspath(String path) {
        File location = new File(path);
        try {
            List<URL> cp = (List<URL>) this.projectProperties.get(ProjectConfigurationEnum.DEPENDENCIES);
            if (!location.exists()) {
                return;
            }

            if (!location.isDirectory()) {
                if (!cp.contains(location.toURI().toURL())) {
                    cp.add(location.toURI().toURL());
                }
            } else {
                cp.add(location.toURI().toURL());
                for (File file : location.listFiles()) {
                    if (file.getName().endsWith(".jar")) {
                        if (!cp.contains(file.toURI().toURL())) {
                            cp.add(file.toURI().toURL());
                        }
                    }
                }
            }
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public List<String> getFailingTestCases() {
        return (List<String>) this.projectProperties.get(ProjectConfigurationEnum.FAILING_TEST_CASES);
    }

    public void setFailingTestCases(List<String> failingTestCases) {
        this.projectProperties.put(ProjectConfigurationEnum.FAILING_TEST_CASES, failingTestCases);
    }

}
