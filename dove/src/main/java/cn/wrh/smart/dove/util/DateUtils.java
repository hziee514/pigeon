package cn.wrh.smart.dove.util;

import android.text.TextUtils;
import android.widget.DatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
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

    public static String getDateForEditor(Date date) {
        if (date == null) {
            return "";
        }
        return dateFormat().format(date);
    }

    public static String getDateForEditor() {
        Calendar c = Calendar.getInstance();
        return dateFormat().format(c.getTime());
    }

    public static Date getDateForEntity(String dt) {
        if (TextUtils.isEmpty(dt)) {
            return null;
        }
        try {
            return dateFormat().parse(dt);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static SimpleDateFormat dateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    }

}
