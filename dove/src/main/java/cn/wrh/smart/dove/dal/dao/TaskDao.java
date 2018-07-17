package cn.wrh.smart.dove.dal.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import cn.wrh.smart.dove.dal.entity.TaskEntity;
import cn.wrh.smart.dove.domain.bo.TaskBO;
import cn.wrh.smart.dove.domain.model.TaskModel;

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

    @Query("SELECT * FROM T_TASK WHERE ID = :id")
    TaskEntity fetch(int id);

    @Query("DELETE FROM T_TASK")
    void clear();

    @Query("SELECT DISTINCT a.id, a.CAGE_ID, b.SERIAL_NUMBER as CAGE_SN, a.EGG_ID, a.TYPE, a.CREATED_AT, a.FINISHED_AT, a.STATUS " +
            "FROM T_TASK a, T_CAGE b " +
            "WHERE a.CAGE_ID = b.ID and date(a.CREATED_AT) = date('now','localtime') " +
            "order by b.SERIAL_NUMBER, type, FINISHED_AT ")
    List<TaskBO> today();

    @Query("SELECT DISTINCT a.id, a.CAGE_ID, b.SERIAL_NUMBER as CAGE_SN, a.EGG_ID, a.TYPE, a.CREATED_AT, a.FINISHED_AT, a.STATUS " +
            "FROM T_TASK a, T_CAGE b " +
            "WHERE a.CAGE_ID = b.ID and a.STATUS = :status and date(a.CREATED_AT) = date('now','localtime') " +
            "order by b.SERIAL_NUMBER, type, FINISHED_AT ")
    List<TaskBO> today(TaskModel.Status status);

}
