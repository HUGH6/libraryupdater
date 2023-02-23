package util;

import core.setup.ProjectConfiguration;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于处理项目信息的门面类
 */
public class ProjectFacade {
    protected ProjectConfiguration setUpProperties = null;

    public ProjectFacade(ProjectConfiguration properties) {
        setProperties(properties);
    }

    public String getOutDirWithPrefix(String currentMutatorIdentifier) {
        return getProperties().getWorkingDirForBytecode() + File.separator + currentMutatorIdentifier;
    }

    public void setProperties(ProjectConfiguration properties) {
        this.setUpProperties = properties;
    }

    public ProjectConfiguration getProperties() {
        return setUpProperties;
    }

    /**
     * Return classpath form mutated variant.
     *
     * @param currentMutatorIdentifier
     * @return
     * @throws MalformedURLException
     */
    public URL[] getClassPathURLforProgramVariant(String currentMutatorIdentifier) throws MalformedURLException {

        List<URL> classpath = new ArrayList<URL>(getProperties().getDependencies());
        // bin
        URL urlBin = new File(getOutDirWithPrefix(currentMutatorIdentifier)).toURI().toURL();
        classpath.add(urlBin);

        URL[] cp = classpath.toArray(new URL[0]);
        return cp;
    }
}
