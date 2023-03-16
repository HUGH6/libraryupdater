package main;

import conf.ConfigurationProperties;
import core.detector.MethodCallDetector;
import core.detector.entity.MethodCallPoint;
import org.apache.log4j.Logger;
import util.IOUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class DetectionMain {
    protected static Logger logger = Logger.getLogger(DetectionMain.class.getSimpleName());

    private MethodCallDetector detector = null;

    public DetectionMain() {
        this.detector = new MethodCallDetector();
    }

    public void execute() throws IOException {
        String projectRoot = ConfigurationProperties.getProperty(ConfigurationProperties.LOCATION);
        String srcDir = ConfigurationProperties.getProperty(ConfigurationProperties.ORIGINAL_DIR_SRC);
        String srcPath = projectRoot + File.separator + srcDir;
        String originApi = ConfigurationProperties.getProperty(ConfigurationProperties.ORIGIN_API);

        List<MethodCallPoint> callPoints = MethodCallDetector.detectMethodCall(srcPath, originApi);

        List<String> callPointPositions = callPoints.stream()
                .map(c -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append(new File(c.file).toURI().getPath());
                    sb.append(":");
                    sb.append(c.line);

                    return sb.toString();
                })
                .collect(Collectors.toList());

        String defaultDetectionOutputFile =  "call_points.txt";
        File outputFile = new File(defaultDetectionOutputFile);

        IOUtil.clearFileContent(outputFile);

        for (String str : callPointPositions) {
            IOUtil.writeToFile(outputFile, str);
            IOUtil.writeToFile(outputFile, "\n");
        }

        logger.info("Detect " + callPointPositions.size() + " call points of api " + originApi);
        logger.info("Output api call points in file: " + outputFile.getAbsolutePath());
    }
}
