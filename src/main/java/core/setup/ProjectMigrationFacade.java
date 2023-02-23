package core.setup;

import conf.ConfigurationProperties;
import conf.ProjectConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import util.IOUtil;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于封装对待迁移项目信息的访问和管理，提供一个门面类
 * 如迁移工作目录、原项目代码目录、字节码目录、单测代码目录、数据目录路径的访问与创建等
 */
public class ProjectMigrationFacade {
    private static Logger logger = Logger.getLogger(Thread.currentThread().getName());

    // 保存当前迁移项目的信息
    protected ProjectConfiguration migrationProjectConfiguration = new ProjectConfiguration();

    /**
     * 迁移项目门面类构造器
     * @param projectConfiguration 保存了迁移项目信息的配置类
     */
    public ProjectMigrationFacade(ProjectConfiguration projectConfiguration) {
        setProjectConfiguration(projectConfiguration);
    }

    /**
     * 初始化迁移工作目录
     * 由于会对迁移项目进行突变、编译、测试，需要储存不同变体的中间结果（字节码等）,因此，针对不同变体，需要单独的工作目录，以避免冲突
     * 总体的迁移工作目录下，按变体标识符划分子文件夹，每个子文件夹内保存特定变体的代码数据和迁移结果
     * @param currentMutatorIdentifier 当前世代程序变体的标识符
     */
    public synchronized void setupWorkingDirectories(String currentMutatorIdentifier) throws IOException {
        // 清空现有的工作目录
        cleanAllMutationResultDirectories();
        // 将源代码复制到工作空间目录下相应代码突变体的目录
        copyOriginalCodeToMutatorDir(currentMutatorIdentifier);

        try {
            // 复制项目编译得到的字节码到工作目录中相应代码突变体的目录下
            copyOriginalBinToMutatorDir(getProjectConfiguration().getOriginalDirBin(), currentMutatorIdentifier);
            // 复制项目单元测试文件编译得到的字节码到工作目录中相应代码突变体的目录下
            copyOriginalBinToMutatorDir(getProjectConfiguration().getOriginalDirTestBin(), currentMutatorIdentifier);
        } catch (Exception e) {
            throw new IOException(e);
        }
        // 复制其他数据
        copyDataToMutatorDir(currentMutatorIdentifier);
    }

    /**
     * 将项目源代码复制到代码变体相应的工作目录下
     * @param mutatorIdentifier 代码变体标识符
     * @throws IOException
     */
    public void copyOriginalCodeToMutatorDir(String mutatorIdentifier) throws IOException {
        List<String> dirs = getProjectConfiguration().getOriginalDirSrc();
        for (String srcDir : dirs) {
            copyOriginalSourceCodeToMutatorDir(srcDir, mutatorIdentifier);
        }
    }

    /**
     * 将项目源代码复制到代码变体相应的工作目录下
     * @param originalCodePath 原始项目源代码路径
     * @param currentMutatorIdentifier 代码变体标识符
     * @throws IOException
     */
    public void copyOriginalSourceCodeToMutatorDir(String originalCodePath, String currentMutatorIdentifier) throws IOException {
        File destination = new File(getProjectConfiguration().getWorkingDirForSourceCode() + File.separator + currentMutatorIdentifier);
        destination.mkdirs();
        FileUtils.copyDirectory(new File(originalCodePath), destination);
    }

    /**
     * 将待迁移项目中的编译字节码复制到特定突变版本的工作目录中
     * @param binDirs 待迁移项目的编译字节码的目录
     * @param mutatorIdentifier 代码变体的标识，用于生成目录
     * @throws IOException
     */
    public void copyOriginalBinToMutatorDir(List<String> binDirs, String mutatorIdentifier) throws IOException {
        if (binDirs == null) {
            logger.debug("original bin folder does not exist");
        }

        for (String dir : binDirs) {
            if (dir != null && !"".equals(dir)) {
                File originalBins = new File(dir);
                File destBins = new File(getMutatorOutDirWithPrefix(mutatorIdentifier));
                destBins.mkdirs();
                FileUtils.copyDirectory(originalBins, destBins);
            }
        }
    }

    /**
     * 清空保存特定程序变体的结果文件夹
     * @param currentMutatorIdentifier 特定世代程序变体标识符
     * @throws IOException
     */
    public void cleanMutationResultDirectories(String currentMutatorIdentifier) throws IOException {
        // 程序变体的工作文件夹是按如下规则创建的：
        // 总体的迁移工作目录下，按变体标识符划分子文件夹，每个子文件夹内保存特定变体的代码数据和迁移结果
        IOUtil.resetDir(getProjectConfiguration().getWorkingDirForSourceCode() + File.separator + currentMutatorIdentifier);
        IOUtil.resetDir(getProjectConfiguration().getWorkingDirForBytecode() + File.separator + currentMutatorIdentifier);
    }

    /**
     * 清空所有程序变体的结果文件夹
     */
    public void cleanAllMutationResultDirectories() {
        IOUtil.resetDir(getProjectConfiguration().getWorkingDirForSourceCode());
        IOUtil.resetDir(getProjectConfiguration().getWorkingDirForBytecode());
    }

    /**
     * 将待迁移项目的数据文件夹复制到代码变体相应的目录下
     * @param currentMutatorIdentifier
     * @throws IOException
     */
    public void copyDataToMutatorDir(String currentMutatorIdentifier) throws IOException {
        String resourceDir = getProjectConfiguration().getOriginalDirData();
        if (resourceDir == null || "".equals(resourceDir)) {
            return;
        }

        String [] resources = resourceDir.split(File.pathSeparator);
        File destDataDir = new File(getMutatorOutDirWithPrefix(currentMutatorIdentifier));

        for (String res : resources) {
            String path = ConfigurationProperties.getProperty(ConfigurationProperties.LOCATION);
            File source = new File(path + File.separator + res);
            if (!source.exists()) {
                return;
            }

            FileUtils.copyDirectory(source, destDataDir);
        }
    }

    /**
     * 返回迁移项目的属性信息
     * @return
     */
    public ProjectConfiguration getProjectConfiguration() {
        return migrationProjectConfiguration;
    }

    public void setProjectConfiguration(ProjectConfiguration projectConfiguration) {
        this.migrationProjectConfiguration = projectConfiguration;
    }

    /**
     * 生成工作目录下特定代码变体专属的目录中用于存放编译字节码的目录
     * @param currentMutatorIdentifier 代码变体标识符，用于生成目录
     * @return
     */
    public String getMutatorOutDirWithPrefix(String currentMutatorIdentifier) {
        return getProjectConfiguration().getWorkingDirForBytecode() + File.separator + currentMutatorIdentifier;
    }

    /**
     * 生成工作目录下特定代码变体专属的目录中用于存放源代码的目录
     * @param currentMutatorIdentifier 代码变体标识符，用于生成目录
     * @return
     */
    public String getMutatorInDirWithPrefix(String currentMutatorIdentifier) {
        return getProjectConfiguration().getWorkingDirForSourceCode() + File.separator + currentMutatorIdentifier;
    }

    /**
     * 返回当前指定程序变体的classpath，包含当前项目编译后的字节码以及依赖jar包
     * @param currentMutatorIdentifier
     * @return
     * @throws MalformedURLException
     */
    public URL[] getClassPathURLForProgramVariant(String currentMutatorIdentifier) throws MalformedURLException {
        // 添加项目依赖到classpath
        List<URL> classpath = new ArrayList<URL>(getProjectConfiguration().getDependencies());
        // 将程序变体突变后的代码字节码加入classpath
        classpath.add(new File(getMutatorOutDirWithPrefix(currentMutatorIdentifier)).toURI().toURL());
        return classpath.toArray(new URL[0]);
    }

    /**
     * 当前待迁移项目的属性信息
     * @return
     */
    public ProjectConfiguration getMigrationProjectConfiguration() {
        return this.migrationProjectConfiguration;
    }

    public void setMigrationProjectConfiguration(ProjectConfiguration configuration) {
        this.migrationProjectConfiguration = configuration;
    }
}
