package core.template.diff.entity;

import java.util.ArrayList;
import java.util.List;

public class ApiElement implements Cloneable{
    public String visibility = "";
    public String qualifiedReturnType = "";
    public String name = "";
    public List<ParamElement> params = new ArrayList<>();
    public List<String> exceptions = new ArrayList<>();
    public boolean isStatic = false;

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getQualifiedReturnType() {
        return qualifiedReturnType;
    }

    public void setQualifiedReturnType(String qualifiedReturnType) {
        this.qualifiedReturnType = qualifiedReturnType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ParamElement> getParams() {
        return params;
    }

    public void setParams(List<ParamElement> params) {
        this.params = params;
    }

    public List<String> getExceptions() {
        return exceptions;
    }

    public void setExceptions(List<String> exceptions) {
        this.exceptions = exceptions;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.visibility.hashCode();
        result = prime * result + this.qualifiedReturnType.hashCode();
        result = prime * result + this.name.hashCode();
        result = prime * result + this.params.hashCode();
        result = prime * result + this.exceptions.hashCode();
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

        ApiElement otherApi = (ApiElement) obj;
        if (this.visibility == null) {
            if (this.visibility != otherApi.getVisibility()) {
                return false;
            }
        } else {
            if (!this.visibility.equals(otherApi.getVisibility())) {
                return false;
            }
        }
        if (this.qualifiedReturnType == null) {
            if (this.qualifiedReturnType != otherApi.getQualifiedReturnType()) {
                return false;
            }
        } else {
            if (!this.qualifiedReturnType.equals(otherApi.getQualifiedReturnType())) {
                return false;
            }
        }
        if (this.name == null) {
            if (this.name != otherApi.getName()) {
                return false;
            }
        } else {
            if (!this.name.equals(otherApi.getName())) {
                return false;
            }
        }
        if (!this.params.equals(otherApi.getParams())) {
            return false;
        }
        if (!this.exceptions.equals(otherApi.getExceptions())) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.qualifiedReturnType + " " + this.name + "(");

        for (ParamElement p : this.params) {
            sb.append(p.toString());
        }
        sb.append(")");

        if (this.exceptions.size() > 0) {
            sb.append(" throws");

            for (String e : this.exceptions) {
                sb.append(" " + e);
            }
        }

        return sb.toString();
    }

    @Override
    public ApiElement clone() {
        ApiElement element = new ApiElement();
        element.setQualifiedReturnType(this.qualifiedReturnType);
        element.setName(this.name);
        element.setExceptions(this.exceptions);
        element.setVisibility(this.visibility);
        element.setStatic(this.isStatic);

        List<ParamElement> params = new ArrayList<>();
        for (ParamElement p : this.params) {
            params.add(p.clone());
        }
        element.setParams(params);

        return element;
    }
}
