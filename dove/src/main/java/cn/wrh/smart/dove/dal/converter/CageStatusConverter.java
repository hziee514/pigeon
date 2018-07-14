package cn.wrh.smart.dove.dal.converter;

import android.arch.persistence.room.TypeConverter;

import cn.wrh.smart.dove.domain.model.CageModel;

/**
 * @author bruce.wu
 * @date 2018/7/14
 */
public class CageStatusConverter {

    @TypeConverter
    public static CageModel.Status toStatus(String status) {
        return status == null ? null : CageModel.Status.valueOf(status);
    }

    @TypeConverter
    public static String fromStatus(CageModel.Status status) {
        return status == null ? null : status.name();
    }

}
