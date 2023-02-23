package util;

import org.eclipse.jdt.internal.compiler.util.Messages;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;

public class ClassFileUtil {
    /**
     * 将类文件写到磁盘
     * @param generatePackagesStructure
     * @param outputPath
     * @param relativeFileName
     * @param bytes
     * @throws IOException
     */
    public static void writeToDisk(boolean generatePackagesStructure, String outputPath, String relativeFileName,
                                   byte[] bytes) throws IOException {

        BufferedOutputStream output = null;
        if (generatePackagesStructure) {
            output = new BufferedOutputStream(
                    new FileOutputStream(new File(buildAllDirectoriesInto(outputPath, relativeFileName))));
        } else {
            String fileName = null;
            char fileSeparatorChar = File.separatorChar;
            String fileSeparator = File.separator;
            // First we ensure that the outputPath exists
            outputPath = outputPath.replace('/', fileSeparatorChar);
            // To be able to pass the mkdirs() method we need to remove the
            // extra file separator at the end of the outDir name
            int indexOfPackageSeparator = relativeFileName.lastIndexOf(fileSeparatorChar);
            if (indexOfPackageSeparator == -1) {
                if (outputPath.endsWith(fileSeparator)) {
                    fileName = outputPath + relativeFileName;
                } else {
                    fileName = outputPath + fileSeparator + relativeFileName;
                }
            } else {
                int length = relativeFileName.length();
                if (outputPath.endsWith(fileSeparator)) {
                    fileName = outputPath + relativeFileName.substring(indexOfPackageSeparator + 1, length);
                } else {
                    fileName = outputPath + fileSeparator
                            + relativeFileName.substring(indexOfPackageSeparator + 1, length);
                }
            }
            output = new BufferedOutputStream(new FileOutputStream(new File(fileName)));
        }
        try {
            output.write(bytes, 0, bytes.length);
        } finally {
            output.flush();
            output.close();
        }
    }

    /**
     * 创建所有的文件夹层级
     * @param outputPath
     * @param relativeFileName
     * @return
     * @throws IOException
     */
    static String buildAllDirectoriesInto(String outputPath, String relativeFileName) throws IOException {
        char fileSeparatorChar = File.separatorChar;
        String fileSeparator = File.separator;
        File f;
        // First we ensure that the outputPath exists
        outputPath = outputPath.replace('/', fileSeparatorChar);
        // To be able to pass the mkdirs() method we need to remove the extra
        // file separator at the end of the outDir name
        if (outputPath.endsWith(fileSeparator)) {
            outputPath = outputPath.substring(0, outputPath.length() - 1);
        }
        f = new File(outputPath);
        if (f.exists()) {
            if (!f.isDirectory()) {
                final String message = Messages.bind(Messages.output_isFile, f.getAbsolutePath());
                throw new IOException(message);
            }
        } else {
            // we have to create that directory
            if (!f.mkdirs()) {
                final String message = Messages.bind(Messages.output_notValidAll, f.getAbsolutePath());
                throw new IOException(message);
            }
        }
        StringBuffer outDir = new StringBuffer(outputPath);
        outDir.append(fileSeparator);
        StringTokenizer tokenizer = new StringTokenizer(relativeFileName, fileSeparator);
        String token = tokenizer.nextToken();
        while (tokenizer.hasMoreTokens()) {
            f = new File(outDir.append(token).append(fileSeparator).toString());
            if (f.exists()) {
                // The outDir already exists, so we proceed the next entry
                // System.out.println("outDir: " + outDir + " already exists.");
            } else {
                // Need to add the outDir
                if (!f.mkdir()) {
                    throw new IOException(Messages.bind(Messages.output_notValid, f.getName()));
                }
            }
            token = tokenizer.nextToken();
        }
        // token contains the last one
        return outDir.append(token).toString();
    }
}
