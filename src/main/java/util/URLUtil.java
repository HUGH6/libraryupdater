package util;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class URLUtil {
    private static Logger logger = Logger.getLogger(URLUtil.class.getSimpleName());

    public static String urlArrayToString(URL[] urls) {
        String[] paths = urlsToAbsolutePaths(urls);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < paths.length; i++) {
            String path = paths[i];
//            sb.append("\"");
            sb.append(path);
//            sb.append("\"");
            sb.append(File.pathSeparator);
        }
        return sb.toString();
    }

    public static String[] urlsToAbsolutePaths(URL[] urls) {
        List<String> paths = new ArrayList<>();
        for (URL url : urls) {
            try {
                String path = new File(url.toURI()).getPath();
                paths.add(path);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return paths.toArray(new String[0]);
    }

    public static List<URL> pathStringToUrlList(String paths) {
        List<URL> urls = new ArrayList<>();
        String [] pathArray = paths.split(File.pathSeparator);
        for (String p : pathArray) {
            if (p == null || p.trim().isEmpty()) {
                continue;
            }

            try {
                urls.add(new File(p).toURI().toURL());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return urls;
    }
}
