package core.template.diff.entity.diff;

import core.template.diff.entity.ApiElement;
import core.template.diff.entity.TransferAction;
import core.template.diff.entity.Diff;
import core.template.diff.entity.ParamElement;
import core.template.diff.entity.action.MoveParamAction;

public class MoveParamDiff implements Diff {
    public ParamElement paramToMove; // 待移动的参数
    public ParamElement moveAfter;   // 移动锚点，将待移动的参数移动到改参数之后

    public ApiElement originApi = null;
    public ApiElement targetApi = null;

    public MoveParamDiff(ApiElement originApi, ApiElement targetApi,ParamElement paramToMove, ParamElement moveAfter) {
        this.originApi = originApi;
        this.targetApi = targetApi;
        this.paramToMove = paramToMove;
        this.moveAfter = moveAfter;
    }

    @Override
    public TransferAction getTransferAction() {
        return new MoveParamAction(this);
    }

    @Override
    public String toString() {
        return "Diff: Remove param " + this.paramToMove + " to position after " + this.moveAfter;
    }
}
