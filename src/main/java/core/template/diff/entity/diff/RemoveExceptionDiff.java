package core.template.diff.entity.diff;

import core.template.diff.entity.ApiElement;
import core.template.diff.entity.TransferAction;
import core.template.diff.entity.Diff;
import core.template.diff.entity.action.RemoveExceptionAction;

public class RemoveExceptionDiff implements Diff {
    public String exceptionToRemove;

    public ApiElement originApi = null;
    public ApiElement targetApi = null;

    public RemoveExceptionDiff(ApiElement originApi, ApiElement targetApi,String exception) {
        this.originApi = originApi;
        this.targetApi = targetApi;
        this.exceptionToRemove = exception;
    }
    @Override
    public TransferAction getTransferAction() {
        return new RemoveExceptionAction(this);
    }

    @Override
    public String toString() {
        return "Diff: Remove exception " + this.exceptionToRemove;
    }
}
