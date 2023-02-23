package core.extractor;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于查找文件夹中的jar文件
 */
public class FindFileVisitor extends SimpleFileVisitor<Path> {
    private String fileSuffix = "";
    private List<String> fileNames = new ArrayList<>();

    public FindFileVisitor(String suffix) {
        super();
        this.fileSuffix = suffix;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (file.toString().endsWith(fileSuffix)) {
            fileNames.add(file.toString());
        }
        return FileVisitResult.CONTINUE;
    }

    public List<String> getFileNameList() {
        return fileNames;
    }
}
