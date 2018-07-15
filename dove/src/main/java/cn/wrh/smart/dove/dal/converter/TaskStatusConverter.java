package cn.wrh.smart.dove.dal.converter;

import android.arch.persistence.room.TypeConverter;

import cn.wrh.smart.dove.domain.model.TaskModel;

/**
 * @author bruce.wu
 * @date 2018/7/15
 */
public class TaskStatusConverter {

    @TypeConverter
    public static TaskModel.Status toStatus(String status) {
        return status == null ? null : TaskModel.Status.valueOf(status);
    }

    @TypeConverter
    public static String fromStatus(TaskModel.Status status) {
        return status == null ? null : status.name();
    }

}
