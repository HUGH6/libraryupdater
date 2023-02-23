package core.template.diff.entity;

import spoon.reflect.declaration.CtElement;

/**
 * 表示对代码的转换操作
 */
public interface TransferAction {
    /**
     * 在代码元素上应用转换
     * @param codeElement 需要转换的代码元素
     * @param context 当前转换所需的上下文信息，不能为空
     * @return 转换后的代码元素
     */
    CtElement transform(CtElement codeElement, ActionContext context);
}
