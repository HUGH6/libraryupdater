package core.manipulation.bytecode.compiler;

import core.entity.ProgramVariant;
import core.manipulation.bytecode.entity.CompilationResult;
import spoon.reflect.declaration.CtType;

import java.net.URL;
import java.util.Collection;

public interface VariantCompiler {
    /**
     * 通过程序变体进行编译
     * @param instanceToCompile
     * @param classpath
     * @return
     */
    CompilationResult compile(ProgramVariant instanceToCompile, URL[] classpath);

    /**
     * 通过类模型进行编译
     * @param classesToCompile
     * @param classpath
     * @return
     */
    CompilationResult compile(Collection<? extends CtType> classesToCompile, URL[] classpath);
}
