package core.template.diff.entity.diff;

import core.template.diff.entity.ApiElement;
import core.template.diff.entity.TransferAction;
import core.template.diff.entity.Diff;
import core.template.diff.entity.action.ReturnTypeChangeAction;

public class ReturnTypeChangeDiff implements Diff {
    public String originReturnType = "";
    public String targetReturnType = "";

    public ApiElement originApi = null;
    public ApiElement targetApi = null;

    public ReturnTypeChangeDiff(ApiElement originApi, ApiElement targetApi,String originReturnType, String targetReturnType) {
        this.originApi = originApi;
        this.targetApi = targetApi;
        this.originReturnType = originReturnType;
        this.targetReturnType = targetReturnType;
    }

    @Override
    public TransferAction getTransferAction() {
        return new ReturnTypeChangeAction(this);
    }

    @Override
    public String toString() {
        return "Diff: Change return type from " + this.originReturnType + " to " + this.targetReturnType;
    }
}
