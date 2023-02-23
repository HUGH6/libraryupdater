package core.template.diff;

import core.template.diff.entity.ApiElement;
import core.template.diff.entity.Diff;
import core.template.diff.entity.ParamElement;
import core.template.diff.entity.TransferAction;
import core.template.diff.entity.diff.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 对比API差异的简单diff算法
 */
public class SimpleApiDiffer {
    /**
     * 对新旧api进行diff，生成diff信息
     * @param origin
     * @param target
     * @return
     */
    public static List<Diff> diff(ApiElement origin, ApiElement target) {
        List<Diff> diffs = new ArrayList<>();

        if (!origin.getQualifiedReturnType().equals(target.getQualifiedReturnType())) {
            diffs.add(new ReturnTypeChangeDiff(origin, target, origin.getQualifiedReturnType(), target.getQualifiedReturnType()));
        }
        if (!origin.getName().equals(target.getName())) {
            diffs.add(new NameChangeDiff(origin, target, origin.getName(), target.getName()));
        }

        diffs.addAll(diffParams(origin, target));

        diffs.addAll(diffExceptions(origin, target));

        return diffs;
    }

    /**
     * 根据差异信息生成转换操作序列
     * @param diffs
     * @return
     */
    public static List<TransferAction> getTransferActionByDiff(List<Diff> diffs) {
        List<TransferAction> actions = new ArrayList<>();
        for (Diff diff : diffs) {
            actions.add(diff.getTransferAction());
        }
        return actions;
    }

    /**
     * 对比方法参数差异
     * @param originApi
     * @param targetApi
     * @return
     */
    private static List<Diff> diffParams(ApiElement originApi, ApiElement targetApi) {
        List<ParamElement> origin = originApi.getParams();
        List<ParamElement> target = targetApi.getParams();

        List<Diff> diffs = new ArrayList<>();

        int maxIndex = 0;
        ParamElement preProcessedParam = null;
        for (ParamElement paramOfTarget : target) {
            int indexInOrigin = origin.indexOf(paramOfTarget);
            if (indexInOrigin != -1) {
                if (indexInOrigin < maxIndex) {
                    // 将当前参数移动到上一个处理的参数后
                    diffs.add(new MoveParamDiff(originApi, targetApi, paramOfTarget, preProcessedParam));
                } else {
                    maxIndex = indexInOrigin;
                }
            } else {
                // 对于新增的参数，添加到上一个处理的参数之后
                diffs.add(new AddParamDiff(originApi, targetApi, paramOfTarget, preProcessedParam));
            }
            preProcessedParam = paramOfTarget;
        }

        for (ParamElement paramOfOrigin : origin) {
            int indexInTarget = target.indexOf(paramOfOrigin);
            if (indexInTarget == -1) {
                diffs.add(new RemoveParamDiff(originApi, targetApi, paramOfOrigin));
            }
        }

        return diffs;
    }

    /**
     * 对比异常
     * @param originApi
     * @param targetApi
     * @return
     */
    private static List<Diff> diffExceptions(ApiElement originApi, ApiElement targetApi) {
        List<String> origin = originApi.getExceptions();
        List<String> target = targetApi.getExceptions();

        List<Diff> diffs = new ArrayList<>();

        Set<String> originExceptions = new HashSet<>(origin);
        Set<String> targetExceptions = new HashSet<>(target);

        // 存放新增的异常
        Set<String> addExceptionsSet = new HashSet<>();
        // 存放删除的异常
        Set<String> removeExceptionsSet = new HashSet<>();

        addExceptionsSet.addAll(targetExceptions);
        addExceptionsSet.removeAll(originExceptions);
        for (String e : addExceptionsSet) {
            diffs.add(new AddExceptionDiff(originApi, targetApi, e));
        }

        removeExceptionsSet.addAll(originExceptions);
        removeExceptionsSet.removeAll(targetExceptions);
        for (String e : removeExceptionsSet) {
            diffs.add(new RemoveExceptionDiff(originApi, targetApi,e));
        }

        return diffs;
    }
}
