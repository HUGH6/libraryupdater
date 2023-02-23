package util;

import conf.ConfigurationProperties;

import java.io.File;

public class MavenUtil {
    /**
     * 获取mvn命令行程序位置
     * @return mvn命令路径
     */
    public static String getMvnCommand() {
        // 从配置文件中查找
        String mvnCommand = ConfigurationProperties.getProperty(ConfigurationProperties.MAVEN_DIR);
        if (mvnCommand == null || "".equals(mvnCommand)) {
            mvnCommand = findExecutableOnPath("mvn");
        }
        return mvnCommand;
    }

    /**
     * 根据程序名称从环境变量中查找程序绝对路径
     * @param name 程序名称
     * @return 程序绝对路径
     */
    public static String findExecutableOnPath(String name) {
        for (String path : System.getenv("PATH").split(File.pathSeparator)) {
            File file = new File(path, name);
            if (file.isFile() && file.canExecute()) {
                return file.getAbsolutePath();
            }
        }
        return null;
    }
}
