package core.setup;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectConfiguration {
    private Map<ProjectPropertiesEnum, Object> internalProperties = new HashMap<>();

    public ProjectConfiguration() {
        this.internalProperties.put(ProjectPropertiesEnum.OriginalDirSrc, new ArrayList<String>());
        this.internalProperties.put(ProjectPropertiesEnum.Dependencies, new ArrayList<String>());
    }

    public void setProperty(ProjectPropertiesEnum key, Object value) {
        this.internalProperties.put(key, value);
    }

    public Object getProperty(ProjectConfiguration key) {
        return this.internalProperties.get(key);
    }

    public String getStringProperty(ProjectConfiguration key) {
        return (String) this.internalProperties.get(key);
    }

    public List<String> getOriginalDirSrc() {
        return (List<String>) this.internalProperties.get(ProjectPropertiesEnum.OriginalDirSrc);
    }

    public String getWorkingDirForBytecode() {
        return (String) this.internalProperties.get(ProjectPropertiesEnum.WorkingDirBytecode);
    }

    public List<URL> getDependencies() {
        return (List<URL>) this.internalProperties.get(ProjectPropertiesEnum.Dependencies);
    }

    public List<String> getFailingTestCases() {
        return (List<String>) this.internalProperties.get(ProjectPropertiesEnum.FailingTestCases);
    }

    public List<String> getRegressionTestCases() {
        return (List<String>) this.internalProperties.get(ProjectPropertiesEnum.RegressionTestCases);
    }
}
