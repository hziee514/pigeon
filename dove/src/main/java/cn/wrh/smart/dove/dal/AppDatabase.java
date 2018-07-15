package cn.wrh.smart.dove.dal;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.support.annotation.VisibleForTesting;

import cn.wrh.smart.dove.dal.converter.CageStatusConverter;
import cn.wrh.smart.dove.dal.converter.DateConverter;
import cn.wrh.smart.dove.dal.converter.EggStageConverter;
import cn.wrh.smart.dove.dal.converter.TaskStatusConverter;
import cn.wrh.smart.dove.dal.converter.TaskTypeConverter;
import cn.wrh.smart.dove.dal.dao.CageDao;
import cn.wrh.smart.dove.dal.dao.EggDao;
import cn.wrh.smart.dove.dal.dao.TaskDao;
import cn.wrh.smart.dove.dal.entity.CageEntity;
import cn.wrh.smart.dove.dal.entity.EggEntity;
import cn.wrh.smart.dove.dal.entity.TaskEntity;

/**
 * @author bruce.wu
 * @date 2018/7/9
 */
@Database(entities = {
        CageEntity.class,
        EggEntity.class,
        TaskEntity.class
},
        version = 1)
@TypeConverters({
        DateConverter.class,
        CageStatusConverter.class,
        EggStageConverter.class,
        TaskTypeConverter.class,
        TaskStatusConverter.class
})
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    @VisibleForTesting
    private static final String DATABASE_NAME = "dove.db";

    public static AppDatabase getInstance(Context appContext) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = build(appContext);
                }
            }
        }
        return instance;
    }

    private static AppDatabase build(final Context appContext) {
        return Room.databaseBuilder(appContext, AppDatabase.class, DATABASE_NAME)
                .build();
    }

    public void exec(String sql, Object...args) {
        mDatabase.execSQL(sql, args);
    }

    public abstract CageDao cageDao();

    public abstract EggDao eggDao();

    public abstract TaskDao taskDao();

}
