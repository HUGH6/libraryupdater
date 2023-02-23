package core.template.diff.entity.diff;

import core.template.diff.entity.ApiElement;
import core.template.diff.entity.Diff;
import core.template.diff.entity.ParamElement;
import core.template.diff.entity.TransferAction;
import core.template.diff.entity.action.AddParamAction;

public class AddParamDiff implements Diff {
    public ParamElement paramToAdd; // 待添加的参数
    public ParamElement addAfter;   // 添加锚点，待添加的参数添加到该参数之后

    public ApiElement originApi = null;
    public ApiElement targetApi = null;

    public AddParamDiff(ApiElement originApi, ApiElement targetApi,ParamElement paramToAdd, ParamElement addAfter) {
        this.originApi = originApi;
        this.targetApi = targetApi;
        this.paramToAdd = paramToAdd;
        this.addAfter  = addAfter;
    }

    @Override
    public TransferAction getTransferAction() {
        return new AddParamAction(this);
    }

    @Override
    public String toString() {
        return "Diff: Add param " + this.paramToAdd + " after param " + this.addAfter;
    }
}
