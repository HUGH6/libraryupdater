package core.setup;

import java.io.InputStream;
import java.util.Properties;

/**
 * 用于存储项目配置参数
 */
public class ConfigurationProperties {
    public static Properties properties = new Properties();
    public static String defaultPropertyFile = "conf.properties";

    static {
        loadPropertiesFromFile();
    }

    protected static void loadPropertiesFromFile() {
        try (InputStream propFile =
                     ConfigurationProperties.class.getClassLoader().getResourceAsStream(defaultPropertyFile)){
            properties.load(propFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean hasProperty(String key) {
        return properties.getProperty(key) != null;
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    /**
     * 以Integer类型返回属性值，若熟悉不存在，会返回0
     * @param key 熟悉名称
     * @return Integer类型的返回值，若熟悉key不存在，则返回0
     */
    public static Integer getPropertyInt(String key) {
        if (properties.getProperty(key) == null) {
            return 0;
        }
        return Integer.valueOf(properties.getProperty(key));
    }

    public static Boolean getPropertyBool(String key) {
        return Boolean.valueOf(properties.getProperty(key));
    }

    public static Double getPropertyDouble(String key) {
        return Double.valueOf(properties.getProperty(key));
    }

    /**
     * Clean/remove all properties, then reload the default properties
     */
    public static void reload() {
        properties.clear();
        loadPropertiesFromFile();
    }
}
