package core.ingredient;

/**
 * 表示修复代码材料的提取范围
 */
public enum IngredientScope {
    BLOCK,  // 调用点最近的代码块
    METHOD, // 调用点所属方法
    CLASS,  // 指定类
    PACKAGE,// 指定包
    GLOBAL; // 项目全局
}
