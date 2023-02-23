package core.extractor;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.*;
import com.github.javaparser.utils.SourceRoot;
import conf.ConfigurationProperties;
import core.setup.PropertyKey;
import dao.MongoDao;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultMethodExtractor implements MethodExtractor {

    private JavaParser javaParser = null;       // 用于分析项目源码
    private SourceRoot sourceRoot = null;

    private String libraryIdentifier = null;    // 项目标识
    private String libraryRoot = null;          // 项目根目录
    private String librarySrcPath = null;       // 项目源码目录
    private String libraryDependencyPath = null;// 项目依赖目录

    public DefaultMethodExtractor() {
        libraryIdentifier     = ConfigurationProperties.getProperty(PropertyKey.LibraryIdentifier);
        libraryRoot           = ConfigurationProperties.getProperty(PropertyKey.LibraryRoot);
        librarySrcPath        = ConfigurationProperties.getProperty(PropertyKey.LibrarySrcPath);
        libraryDependencyPath = ConfigurationProperties.getProperty(PropertyKey.LibraryDependencyPath);

        javaParser = new JavaParser(new ParserConfiguration());
    }

    /**
     * 进行初始化
     * （1）设置解析器
     */
    public void init() throws Exception {
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
            throw new IllegalStateException("please init extractor first");
        }

        List<ParseResult<CompilationUnit>> parseResults = sourceRoot.tryToParseParallelized();

        List<MethodDeclaration> methodDeclarations = parseResults.stream()
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

    /**
     * test main
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        MethodExtractor extractor = new DefaultMethodExtractor();
        extractor.init();
        List<MethodInfo> res = extractor.extract();

        for (MethodInfo info : res) {
            System.out.println(MongoDao.toJson(info));
        }
    }
}
