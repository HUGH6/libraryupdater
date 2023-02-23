package core.detector;

import conf.ConfigurationProperties;
import core.detector.entity.MethodCallPoint;
import core.template.diff.entity.ApiElement;
import core.template.diff.entity.ParamElement;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtStatement;
import spoon.reflect.visitor.filter.AbstractFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MethodCallDetector {
    /**
     * 提取在rootPath下项目中对api的调用点
     * @param rootPath 项目路径
     * @param api api签名（包含返回值和签名）, 形式如 boolean test.Demo.test2(java.lang.String)
     * @return
     */
    public static List<MethodCallPoint> detectMethodCall(String rootPath, String api) {
        List<MethodCallPoint> callPoints = new ArrayList<>();
        if (!(new File(rootPath)).exists()) {
            return callPoints;
        }

        SpoonAPI spoon = new Launcher();
        spoon.addInputResource(rootPath);
        spoon.buildModel();

        callPoints = detectMethodCall(spoon.getModel(), api);

        return callPoints;
    }

    /**
     * 直接通过已有的model提取api调用点
     * @param model
     * @param api
     * @return
     */
    public static List<MethodCallPoint> detectMethodCall(CtModel model, String api) {
        List<MethodCallPoint> callPoints = new ArrayList<>();

        api = api.trim();
        int splitIdx = api.indexOf(" ");

        String targetReturnType = api.substring(0, splitIdx);
        String targetSignature = api.substring(splitIdx + 1);

        List<CtInvocation> elements = model.getRootPackage().getElements(new AbstractFilter<CtInvocation>() {
            @Override
            public boolean matches(CtInvocation element) {
                String returnType = element.getExecutable().getType().getQualifiedName();

                String classOfMethod = element.getExecutable().getDeclaringType().getQualifiedName();
                String methodSignature = element.getExecutable().getSignature();
                String fullSignature = classOfMethod + "." + methodSignature;

                // 返回值类型和方法全限定签名相同
                if (returnType.equals(targetReturnType) && fullSignature.equals(targetSignature)) {
                    return true;
                }

                return false;
            }
        });

        // 转化为调用点对象表示
        for (CtInvocation e : elements) {
            callPoints.add(new MethodCallPoint(e));
        }

        return callPoints;
    }

    public static List<MethodCallPoint> detectMethodCall(CtModel model, ApiElement api) {
        List<MethodCallPoint> callPoints = new ArrayList<>();

        String targetReturnType = api.qualifiedReturnType;
        String targetMethodName = api.name;
        String targetSignature = targetMethodName + "(";
        for (ParamElement p : api.params) {
            targetSignature += p.qualifiedType + ",";
        }
        targetSignature = targetSignature.substring(0, targetSignature.length() - 1);
        targetSignature = targetSignature + ")";

        String finalTargetSignature = targetSignature;

        List<CtInvocation> elements = model.getRootPackage().getElements(new AbstractFilter<CtInvocation>() {
            @Override
            public boolean matches(CtInvocation element) {
                String returnType = element.getExecutable().getType().getQualifiedName();

                String classOfMethod = element.getExecutable().getDeclaringType().getQualifiedName();
                String methodSignature = element.getExecutable().getSignature();
                String fullSignature = classOfMethod + "." + methodSignature;

                // 返回值类型和方法全限定签名相同
                if (returnType.equals(targetReturnType) && fullSignature.equals(finalTargetSignature)) {
                    return true;
                }

                return false;
            }
        });

        // 转化为调用点对象表示
        for (CtInvocation e : elements) {
            callPoints.add(new MethodCallPoint(e));
        }

        return callPoints;
    }
    /**
     * 测试功能使用
     * @param args
     */
    public static void main(String[] args) {
        String projectRoot = ConfigurationProperties.getProperty(ConfigurationProperties.LOCATION);
        String srcDir = ConfigurationProperties.getProperty(ConfigurationProperties.ORIGINAL_DIR_SRC);
        String srcPath = projectRoot + File.separator + srcDir;
        String originApi = ConfigurationProperties.getProperty(ConfigurationProperties.ORIGIN_API);

//        String path = "E:\\projects\\libraryupdater\\src\\main\\resources\\test\\Demo.java";
//        String api = "boolean test.Demo.test2(java.lang.String)";
        List<MethodCallPoint> callPoints = MethodCallDetector.detectMethodCall(srcPath, originApi);
        callPoints.stream().forEach(System.out::println);

        List<CtStatement> statementList = callPoints.stream().map(c -> (CtStatement)c.callPoint).collect(Collectors.toList());
        statementList.forEach(System.out::println);
    }
}
