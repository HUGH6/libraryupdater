package util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Converters {
    /**
     * classpath数组转对应URL数组
     * @param cp
     * @return
     * @throws MalformedURLException
     */
    public static URL[] toURLArray(String[] cp) throws MalformedURLException {
        List<URL> urls = new ArrayList<URL>();

        for (String c : cp) {
            urls.add(new File(c).toURI().toURL());
        }
        URL[] u = new URL[urls.size()];

        return (URL[]) urls.toArray(u);
    }

    /**
     * 将File转换为对应的url，然后添加到数组中
     * @param foutgen
     * @param originalURL
     * @return
     * @throws MalformedURLException
     */
    public static URL[] redefineURL(File foutgen, URL[] originalURL) throws MalformedURLException {
        List<URL> urls = new ArrayList<URL>();
        urls.add(foutgen.toURL());
        for (int i = 0; (originalURL != null) && i < originalURL.length; i++) {
            urls.add(originalURL[i]);
        }

        return (URL[]) urls.toArray(originalURL);
    }
}
