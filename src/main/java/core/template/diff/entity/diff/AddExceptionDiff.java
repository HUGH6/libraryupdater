package core.template.diff.entity.diff;

import core.template.diff.entity.ApiElement;
import core.template.diff.entity.TransferAction;
import core.template.diff.entity.Diff;
import core.template.diff.entity.action.AddExceptionAction;

public class AddExceptionDiff implements Diff {
    public String exceptionToAdd = "";
    public ApiElement originApi = null;
    public ApiElement targetApi = null;

    public AddExceptionDiff(ApiElement originApi, ApiElement targetApi, String exception) {
        this.originApi = originApi;
        this.targetApi = targetApi;
        this.exceptionToAdd = exception;
    }

    @Override
    public TransferAction getTransferAction() {
        return new AddExceptionAction(this);
    }

    @Override
    public String toString() {
        return "Diff: Add Exception " + this.exceptionToAdd;
    }
}
