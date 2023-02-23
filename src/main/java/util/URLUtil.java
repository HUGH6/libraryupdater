package util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class URLUtil {
    public static String urlArrayToString(URL[] urls) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < urls.length; i++) {
            URL url = urls[i];
            sb.append("\"");
            sb.append(url.getPath());
            sb.append("\"");
            sb.append(File.pathSeparator);
        }
        return sb.toString();
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
