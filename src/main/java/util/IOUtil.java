package util;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;

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
}
