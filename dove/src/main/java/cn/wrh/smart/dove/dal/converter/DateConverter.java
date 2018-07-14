package cn.wrh.smart.dove.dal.converter;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * @author bruce.wu
 * @date 2018/7/9
 */
public class DateConverter {

    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

}
