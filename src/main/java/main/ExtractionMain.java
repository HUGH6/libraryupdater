package main;

import core.extractor.DefaultMethodExtractor;
import core.extractor.MethodInfo;
import dao.MongoDao;
import org.apache.log4j.Logger;
import util.IOUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ExtractionMain {
    protected static Logger logger = Logger.getLogger(ExtractionMain.class.getSimpleName());

    private DefaultMethodExtractor extractor;

    public ExtractionMain() {
        this.extractor = new DefaultMethodExtractor();
    }

    public void execute() throws IOException {
        try {
            this.extractor.init();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        List<MethodInfo> res = this.extractor.extract();

        String defaultExtractorOutputFile =  "extract_apis.txt";
        File outputFile = new File(defaultExtractorOutputFile);

        IOUtil.clearFileContent(outputFile);

        for (MethodInfo info : res) {
            IOUtil.writeToFile(outputFile, MongoDao.toJson(info));
            IOUtil.writeToFile(outputFile, "\n");
        }

        logger.info("Extract " + res.size() + " apis");
        logger.info("Output extract apis info in file: " + outputFile.getAbsolutePath());
    }
}
