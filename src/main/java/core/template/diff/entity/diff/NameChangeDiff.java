package core.template.diff.entity.diff;

import core.template.diff.entity.ApiElement;
import core.template.diff.entity.TransferAction;
import core.template.diff.entity.Diff;
import core.template.diff.entity.action.NameChangeAction;

public class NameChangeDiff implements Diff {
    public String originName = "";
    public String targetName = "";

    public ApiElement originApi = null;
    public ApiElement targetApi = null;

    public NameChangeDiff(ApiElement originApi, ApiElement targetApi,String originName, String targetName) {
        this.originApi = originApi;
        this.targetApi = targetApi;
        this.originName = originName;
        this.targetName = targetName;
    }

    @Override
    public TransferAction getTransferAction() {
        return new NameChangeAction(this);
    }

    @Override
    public String toString() {
        return "Diff: Change name from " + this.originName + " to " + this.targetName;
    }
}
