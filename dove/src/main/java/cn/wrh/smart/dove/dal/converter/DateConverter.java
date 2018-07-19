package cn.wrh.smart.dove.dal.converter;

import android.arch.persistence.room.TypeConverter;
import android.text.TextUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author bruce.wu
 * @date 2018/7/9
 */
public class DateConverter {

    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @TypeConverter
    public static Date toDate(String timestamp) {
        try {
            return TextUtils.isEmpty(timestamp)
                    ? null
                    : new SimpleDateFormat(DATETIME_FORMAT, Locale.getDefault()).parse(timestamp);
        } catch (ParseException e) {
            Log.e("DateConverter", timestamp, e);
            return null;
        }
    }

    @TypeConverter
    public static String toTimestamp(Date date) {
        return date == null ? null : new SimpleDateFormat(DATETIME_FORMAT, Locale.getDefault()).format(date);
    }

}
