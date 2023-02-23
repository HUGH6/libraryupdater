package core.extractor;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;

import java.util.Optional;

public class MethodInfo {
    public String className;
    public String signature;
    public String methodName;
    public String returnType;
    public String javadoc;
    public String bodyContent;
    public String qualifiedSignature;
    public String qualifiedMethodName;
    public String qualifiedReturnType;

    public static MethodInfo getMethodInfo(MethodDeclaration md) {
        String javadoc = "";
        Optional<Javadoc> doc = md.getJavadoc();
        if (doc.isPresent()) {
            javadoc = doc.get().toText();
        }

        String methodName = md.getName().toString();
        String returnType = md.getType().toString();
        String signature = md.getSignature().toString();

        String bodyContent = "";
        Optional<BlockStmt> bodyStat = md.getBody();
        if (bodyStat.isPresent()) {
            bodyContent = bodyStat.get().toString();
        }

        String className = "";
        String qualifiedMethodName = "";
        String qualifiedSignature = "";
        String qualifiedReturnType = "";
        try {
            ResolvedMethodDeclaration rmd = md.resolve();
            className = rmd.getClassName();
            qualifiedMethodName = rmd.getQualifiedName();
            qualifiedSignature = rmd.getQualifiedSignature();
            qualifiedReturnType = rmd.getReturnType().describe();
        } catch (UnsolvedSymbolException e) {
            e.printStackTrace();
            return null;
        }

        MethodInfo info = new MethodInfo();
        info.javadoc = javadoc;
        info.methodName = methodName;
        info.returnType = returnType;
        info.signature = signature;
        info.bodyContent = bodyContent;
        info.className = className;
        info.qualifiedMethodName = qualifiedMethodName;
        info.qualifiedSignature = qualifiedSignature;
        info.qualifiedReturnType = qualifiedReturnType;

        return info;
    }
}