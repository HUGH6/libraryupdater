package core.template.diff.entity;

public class ActionContext {
    // 记录原api信息
    public ApiElement originApi = null;
    // 记录目标api信息
    public ApiElement targetApi = null;
    // 记录当前经过转换的原api信息状态
    public ApiElement modifiedApi = null;

    public ActionContext(ApiElement originApi, ApiElement targetApi) {
        this.originApi = originApi;
        this.targetApi = targetApi;
        this.modifiedApi = originApi.clone();
    }
}
