package core.ingredient;

import spoon.reflect.declaration.CtElement;

/**
 * 表示修复代码材料
 * 用于表示调用点上下文中的局部变量、表达式、成员变量
 */
public class Ingredient {
    // 修复材料对应的代码元素
    protected CtElement ingredientCode;
    // 代码元素对应的字符串缓存
    protected String cacheString = null;
    // 名称，用于搜索匹配
    protected String name;

    public Ingredient(CtElement element) {
        super();
        this.ingredientCode = element;
    }

    public Ingredient(CtElement element, String name) {
        super();
        this.ingredientCode = element;
        this.name = name;
    }

    /***************************************
     * getter and setter
     **************************************/
    public CtElement getCode() {
        return ingredientCode;
    }

    public void setCode(CtElement ingredientCode) {
        this.ingredientCode = ingredientCode;
    }

    public String getCacheCodeString() {
        if (cacheString == null && this.getCode() != null) {
            cacheString = this.getCode().toString();
        }
        return cacheString;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + ":" + this.getCode().toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ingredientCode == null) ? 0 : ingredientCode.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Ingredient other = (Ingredient) obj;
        if (ingredientCode == null) {
            if (other.ingredientCode != null) {
                return false;
            }
        } else if (!ingredientCode.equals(other.ingredientCode)) {
            return false;
        }
        return true;
    }
}
