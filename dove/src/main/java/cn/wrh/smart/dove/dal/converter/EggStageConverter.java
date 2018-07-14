package cn.wrh.smart.dove.dal.converter;

import android.arch.persistence.room.TypeConverter;

import cn.wrh.smart.dove.domain.model.EggModel;

/**
 * @author bruce.wu
 * @date 2018/7/14
 */
public class EggStageConverter {

    @TypeConverter
    public static EggModel.Stage toStage(String stage) {
        return stage == null ? null : EggModel.Stage.valueOf(stage);
    }

    @TypeConverter
    public static String fromStage(EggModel.Stage stage) {
        return stage == null ? null : stage.name();
    }

}
