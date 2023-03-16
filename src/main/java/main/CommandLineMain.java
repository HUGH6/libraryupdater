package main;

import conf.ConfigurationProperties;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * 再MigrationMain的基础上进行封装，封装为命令行
 */
public class CommandLineMain {
    protected static Logger logger = Logger.getLogger(CommandLineMain.class.getSimpleName());

    // 命令行参数
    private static Options cmdOptions = new Options();
    // 命令行解析器
    private CommandLineParser cmdParser = new DefaultParser();

    private MigrationMain migrationMain = null;
    private ExtractionMain extractionMain = null;
    private DetectionMain detectionMain = null;

    static {
        cmdOptions.addOption("mode", true, "Execution mode, support 3 mode: migrate/extract/detect");
        cmdOptions.addOption("file", true, "Migration setting file path");
        cmdOptions.addOption("help", false, "Print help info of command line");
    }

    public CommandLineMain() {
        this.migrationMain = new MigrationMain();
    }

    public static void main(String[] args) throws Exception {
        logger.info("Running on a JDK at " + System.getProperty("java.home"));

        CommandLineMain main = new CommandLineMain();
        main.execute(args);
    }

    public void execute(String[] args) throws Exception {

        if (!processArguments(args)) {
            return;
        }

        if (isMigrateMode(args)) {
            runMigrateMode();
            return;
        }

        if (isExtractMode(args)) {
            runExtractMode();
            return;
        }

        if (isDetectMode(args)) {
            runDetectMode();
            return;
        }
    }

    /**
     * 解析命令行参数
     * @param args
     * @return
     */
    private boolean processArguments(String[] args) {
        CommandLine cmd = null;
        try {
            cmd = cmdParser.parse(cmdOptions, args);
        } catch (ParseException e) {
            logger.error("Error: " + e.getMessage());
            help();
            return false;
        }

        if (cmd.hasOption("help")) {
            help();
            return false;
        }

        // 从文件加载配置信息
        if (cmd.hasOption("file")) {
            String settingFilePath = cmd.getOptionValue("file");
            File settingFile = new File(settingFilePath);
            if (!settingFile.exists() || !settingFile.isFile()) {
                logger.error("Invalid file path " + settingFilePath);
                return false;
            }
            ConfigurationProperties.loadPropertiesFromFile(settingFile);
        }
        return true;
    }

    private boolean isMigrateMode(String[] args) {
        CommandLine cmd = null;
        try {
            cmd = cmdParser.parse(cmdOptions, args);
            if (cmd.hasOption("mode")
                    && cmd.getOptionValue("mode").equals("migrate")) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isExtractMode(String[] args) {
        CommandLine cmd = null;
        try {
            cmd = cmdParser.parse(cmdOptions, args);
            if (cmd.hasOption("mode")
                    && cmd.getOptionValue("mode").equals("extract")) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isDetectMode(String[] args) {
        CommandLine cmd = null;
        try {
            cmd = cmdParser.parse(cmdOptions, args);
            if (cmd.hasOption("mode")
                    && cmd.getOptionValue("mode").equals("detect")) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void runMigrateMode() {
        try {
            this.migrationMain.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void runExtractMode() {
        this.extractionMain = new ExtractionMain();
        try {
            this.extractionMain.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runDetectMode() {
        this.detectionMain = new DetectionMain();
        try {
            this.detectionMain.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void help() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Main", cmdOptions);
        System.exit(0);
    }
}
