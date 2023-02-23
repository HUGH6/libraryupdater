package core.template.diff.entity.diff;

import core.template.diff.entity.ApiElement;
import core.template.diff.entity.TransferAction;
import core.template.diff.entity.Diff;
import core.template.diff.entity.ParamElement;
import core.template.diff.entity.action.RemoveParamAction;

public class RemoveParamDiff implements Diff {
    public ParamElement paramToRemove;

    public ApiElement originApi = null;
    public ApiElement targetApi = null;

    public RemoveParamDiff(ApiElement originApi, ApiElement targetApi,ParamElement paramToRemove) {
        this.originApi = originApi;
        this.targetApi = targetApi;
        this.paramToRemove = paramToRemove;
    }

    @Override
    public TransferAction getTransferAction() {
        return new RemoveParamAction(this);
    }

    @Override
    public String toString() {
        return "Diff: remove param " + this.paramToRemove;
    }
}
