package cn.wrh.smart.dove.dal.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Update;

import cn.wrh.smart.dove.dal.entity.TaskEntity;

/**
 * @author bruce.wu
 * @date 2018/7/14
 */
@Dao
public interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TaskEntity...entities);

    @Update
    void update(TaskEntity...entities);

    @Delete
    void delete(TaskEntity...entities);

}
