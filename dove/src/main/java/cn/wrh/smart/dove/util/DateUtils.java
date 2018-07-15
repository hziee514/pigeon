package cn.wrh.smart.dove.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * @author bruce.wu
 * @date 2018/7/14
 */
public class DateUtils {

    public static DateTime now() {
        return DateTime.now(DateTimeZone.UTC);
    }

}
