package core.extractor;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.*;
import com.github.javaparser.utils.SourceRoot;
import conf.ConfigurationProperties;
import org.apache.log4j.Logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultMethodExtractor implements MethodExtractor {
    protected static Logger logger = Logger.getLogger(DefaultMethodExtractor.class.getSimpleName());

    private SourceRoot sourceRoot = null;
    private String libraryIdentifier = null;    // 项目标识
    private String libraryRoot = null;          // 项目根目录
    private String librarySrcPath = null;       // 项目源码目录
    private String libraryDependencyPath = null;// 项目依赖目录

    public DefaultMethodExtractor() {
        libraryIdentifier     = ConfigurationProperties.getProperty(ConfigurationProperties.LIBRARY_IDENTIFIER);
        libraryRoot           = ConfigurationProperties.getProperty(ConfigurationProperties.LIBRARY_ROOT_PATH);
        librarySrcPath        = ConfigurationProperties.getProperty(ConfigurationProperties.LIBRARY_SRC_PATH);
        libraryDependencyPath = ConfigurationProperties.getProperty(ConfigurationProperties.LIBRARY_DEPENDENCY_PATH);
    }

    /**
     * 进行初始化，必须先调用init方法
     */
    public void init() throws Exception {
        logger.info("Init default method extractor");

        logger.info("Library identifier: " + this.libraryIdentifier);
        logger.info("Library root path: " + this.libraryRoot);
        logger.info("Library source path: " + this.librarySrcPath);
        logger.info("Library dependencies jar directory path: " + this.libraryDependencyPath);

        this.sourceRoot = new SourceRoot(new File(librarySrcPath).toPath());

        CombinedTypeSolver typeSolver = new CombinedTypeSolver();
        typeSolver.add(new ReflectionTypeSolver());
        typeSolver.add(new JavaParserTypeSolver(librarySrcPath));

        FindFileVisitor findJarVisitor = new FindFileVisitor(".jar");
        Files.walkFileTree(Paths.get(libraryDependencyPath), findJarVisitor);
        for (String name : findJarVisitor.getFileNameList()) {
            typeSolver.add(new JarTypeSolver(name));
        }
        typeSolver.add(new ClassLoaderTypeSolver(DefaultMethodExtractor.class.getClassLoader()));

        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
        sourceRoot.getParserConfiguration().setSymbolResolver(symbolSolver);
    }

    @Override
    public List<MethodInfo> extract() throws IllegalStateException {
        if (sourceRoot == null) {
            logger.error("Please init the extractor first, invoke the 'init()' before 'extract()'");
            throw new IllegalStateException("Please init extractor first");
        }

        logger.info("Start extract method info");

        List<ParseResult<CompilationUnit>> parseResults = sourceRoot.tryToParseParallelized();

        List<MethodDeclaration> methodDeclarations = parseResults.stream()
                .filter(res -> res != null && res.isSuccessful() && res.getResult().isPresent())
                .map(res -> res.getResult().get())
                .flatMap(compilationUnit -> {
                    return compilationUnit.findAll(MethodDeclaration.class).stream();
                })
                .collect(Collectors.toList());

        List<MethodInfo> methodInfos = batchToMethodInfo(methodDeclarations);

        return methodInfos;
    }

    public static List<MethodInfo> batchToMethodInfo(List<MethodDeclaration> methodDeclarations) {
        return methodDeclarations.parallelStream()
                .map(methodDeclaration -> MethodInfo.getMethodInfo(methodDeclaration))
                .collect(Collectors.toList());
    }

//    /**
//     * test main
//     * @param args
//     * @throws Exception
//     */
//    public static void main(String[] args) throws Exception {
//        MethodExtractor extractor = new DefaultMethodExtractor();
//        extractor.init();
//        List<MethodInfo> res = extractor.extract();
//
//        String defaultExtractorOutputFile =  "extract_apis.txt";
//        File outputFile = new File(defaultExtractorOutputFile);
//
//        IOUtil.clearFileContent(outputFile);
//
//        for (MethodInfo info : res) {
//            IOUtil.writeToFile(outputFile, MongoDao.toJson(info));
//            IOUtil.writeToFile(outputFile, "\n");
//        }
//
//        logger.info("Extract " + res.size() + " apis");
//        logger.info("Output extract apis info in file: " + outputFile.getAbsolutePath());
//    }
}
