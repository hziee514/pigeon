package cn.wrh.smart.dove.util;

import android.text.TextUtils;
import android.widget.DatePicker;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * @author bruce.wu
 * @date 2018/7/14
 */
public class DateUtils {

    public static DateTime now() {
        return DateTime.now(DateTimeZone.UTC);
    }

    public static Calendar getCalendarForEditor(String date) {
        Calendar c = Calendar.getInstance();
        if (!TextUtils.isEmpty(date)) {
            try {
                c.setTime(dateFormat().parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return c;
    }

    public static String getDateForEditor(DatePicker picker) {
        Calendar c = Calendar.getInstance();
        c.set(picker.getYear(), picker.getMonth(), picker.getDayOfMonth());
        return dateFormat().format(c.getTime());
    }

    private static SimpleDateFormat dateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    }

}
