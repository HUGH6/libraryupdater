package core.detector;

import conf.ConfigurationProperties;
import core.detector.entity.MethodCallPoint;
import core.template.diff.ApiElementBuilder;
import core.template.diff.entity.ApiElement;
import core.template.diff.entity.ParamElement;
import org.apache.log4j.Logger;
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
    protected Logger logger = Logger.getLogger(MethodCallDetector.class.getSimpleName());

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

        ApiElement originApiElement = ApiElementBuilder.buildApiElement(api);

        return detectMethodCall(model, originApiElement);
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


        List<MethodCallPoint> callPoints = MethodCallDetector.detectMethodCall(srcPath, originApi);
//        callPoints.stream().forEach(System.out::println);

        List<CtStatement> statementList = callPoints.stream().map(c -> (CtStatement)c.callPoint).collect(Collectors.toList());
//        statementList.forEach(System.out::println);

        List<String> callPointPosition = callPoints.stream()
                .map(c -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append(new File(c.file).toURI().getPath());
                    sb.append(":");
                    sb.append(c.line);

                    return sb.toString();
                })
                .collect(Collectors.toList());

        for (String callPointStr : callPointPosition) {
            System.out.println(callPointStr);
        }
    }
}
