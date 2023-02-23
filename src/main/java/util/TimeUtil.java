package util;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeUtil {
    /**
     * 判断当前时间距离起始时间是否小于最大执行时间
     * @param startTime 程序起始时间
     * @param maxMinutes 程序最大执行时间
     * @return
     */
    public static boolean blowMaxTime(Date startTime, int maxMinutes) {
        Date currentTime = new Date();
        long deltaTimeMillies = currentTime.getTime() - startTime.getTime();
        long deltaTimeMinutes = TimeUnit.MINUTES.convert(deltaTimeMillies, TimeUnit.MILLISECONDS);
        return deltaTimeMinutes < maxMinutes;
    }
}
