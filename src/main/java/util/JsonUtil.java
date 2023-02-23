package util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.File;
import java.util.Iterator;

public class JsonUtil {
    /**
     * 拼接jsonarray中的多个对象为字符串
     * @param array
     * @return
     */
    public static String join(JsonArray array) {
        StringBuilder result = new StringBuilder();
        Iterator<JsonElement> it = array.iterator();

        while (it.hasNext()) {
            result.append(it.next().getAsString());
            if (it.hasNext()) {
                result.append(File.pathSeparator);
            }
        }

        return result.toString();
    }
}
