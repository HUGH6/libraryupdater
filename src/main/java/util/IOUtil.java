package util;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class IOUtil {
    private static final Logger logger = Logger.getLogger(IOUtil.class.getSimpleName());

    /**
     * 将文件夹重置（清空文件夹内部所有数据）
     * @param dir
     */
    public static void resetDir(String dir) {
        File dirToReset = new File(dir);
        try {
            FileUtils.deleteDirectory(dirToReset);
        } catch (Exception e) {
            logger.error("remove dir error", e);
        }
        dirToReset.mkdir();
    }

    public static void appendToFile(String filepath, String content) throws IOException {
        try (FileWriter fileWriter = new FileWriter(filepath)) {
            fileWriter.append(content);
        }
    }

    public static void writeToFile(File filepath, String content) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(filepath, true)) {
            byte[] bytes = content.getBytes();
            fileOutputStream.write(bytes);
        }
    }

    public static void clearFileContent(File filepath) {
        try (FileWriter fileWriter = new FileWriter(filepath)){
            fileWriter.write("");// 清空
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
