package core.template.diff.entity;

public class ParamElement implements Cloneable{
    public String qualifiedType = "";    // 方法参数类型的全限定名称
    public String name = "";             // 方法参数的名称
    public int position = -1;            // 方法参数的位置

    public ParamElement() {
    }

    public ParamElement(String qualifiedType, String name, int position) {
        this.qualifiedType = qualifiedType;
        this.name = name;
        this.position = position;
    }

    public String getQualifiedType() {
        return qualifiedType;
    }

    public void setQualifiedType(String qualifiedType) {
        this.qualifiedType = qualifiedType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.qualifiedType.hashCode();
        result = prime * result + this.name.hashCode();
        result = prime * result + this.position;
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
        if (!(obj instanceof ParamElement)) {
            return false;
        }

        ParamElement otherParam = (ParamElement) obj;
        if (this.qualifiedType == null) {
            if (this.qualifiedType != otherParam.getQualifiedType()) {
                return false;
            }
        } else {
            if (!this.qualifiedType.equals(otherParam.getQualifiedType())) {
                return false;
            }
        }
        if (this.name == null) {
            if (this.name != otherParam.getName()) {
                return false;
            }
        } else {
            if (!this.name.equals(otherParam.getName())) {
                return false;
            }
        }
        if (this.position != otherParam.getPosition()) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return this.position + ": " + this.qualifiedType + " " + this.name + " ";
    }

    @Override
    public ParamElement clone() {
        return new ParamElement(this.qualifiedType, this.name, this.position);
    }
}
