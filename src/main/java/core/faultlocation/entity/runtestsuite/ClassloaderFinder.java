package core.faultlocation.entity.runtestsuite;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class ClassloaderFinder implements ClassFinder {
    private URLClassLoader urlClassLoader;

    public ClassloaderFinder(URLClassLoader urlClassLoader) {
        this.urlClassLoader = urlClassLoader;
    }

    @Override
    public String[] getClasses() {
        List<String> classes = new ArrayList<>();
        for (URL url : urlClassLoader.getURLs()) {
            if (new File(url.getPath()).isDirectory()) {
                classes.addAll(SourceFolderFinder.getClassesLoc(new File(url.getPath()), null));
            }
        }

        return classes.toArray(new String[0]);
    }
}
