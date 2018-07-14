package cn.wrh.smart.dove.dal.converter;

import android.arch.persistence.room.TypeConverter;

import cn.wrh.smart.dove.domain.model.TaskModel;

/**
 * @author bruce.wu
 * @date 2018/7/14
 */
public class TaskTypeConverter {

    @TypeConverter
    public static TaskModel.Type toType(String type) {
        return type == null ? null : TaskModel.Type.valueOf(type);
    }

    @TypeConverter
    public static String fromType(TaskModel.Type type) {
        return type == null ? null : type.name();
    }

}
