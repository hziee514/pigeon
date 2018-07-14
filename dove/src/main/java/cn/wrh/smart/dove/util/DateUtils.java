package cn.wrh.smart.dove.util;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author bruce.wu
 * @date 2018/7/14
 */
public class DateUtils {

    public static Date now() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        return calendar.getTime();
    }

}
